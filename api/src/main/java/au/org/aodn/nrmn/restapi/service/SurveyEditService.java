package au.org.aodn.nrmn.restapi.service;

import static au.org.aodn.nrmn.restapi.util.SpacialUtil.getDistanceLatLongMeters;
import static au.org.aodn.nrmn.restapi.util.TimeUtils.parseDate;
import static au.org.aodn.nrmn.restapi.util.TimeUtils.parseTime;

import java.sql.Time;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import au.org.aodn.nrmn.restapi.repository.ProgramRepository;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationError;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationErrors;
import au.org.aodn.nrmn.restapi.dto.survey.SurveyDto;
import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;

@Service
public class SurveyEditService {

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    DiverRepository diverRepository;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    ModelMapper mapper;

    private static final int RLS_PROGRAM_ID = 1;
    private static final int ATRC_PROGRAM_ID = 2;

    public Survey updateSurvey(SurveyDto surveyDto) {

        Survey survey = surveyRepository.findById(surveyDto.getSurveyId()).orElseThrow(ResourceNotFoundException::new);

        survey.setVisibility(StringUtils.isNotEmpty(surveyDto.getVisibility()) ? Double.valueOf(surveyDto.getVisibility()) : null);
        survey.setDirection(surveyDto.getDirection());
        survey.setLongitude(StringUtils.isNotEmpty(surveyDto.getLongitude()) ? Double.valueOf(surveyDto.getLongitude()) : null);
        survey.setLatitude(StringUtils.isNotEmpty(surveyDto.getLatitude()) ? Double.valueOf(surveyDto.getLatitude()) : null);

        Site site = siteRepository.findBySiteCode(surveyDto.getSiteCode());
        if(!site.getSiteName().equals(surveyDto.getSiteName())) {
            site.setSiteName(surveyDto.getSiteName());
            siteRepository.save(site);
        }
        survey.setSite(site);

        survey.setProgram(programRepository.findById(surveyDto.getProgramId()).orElseThrow(ResourceNotFoundException::new));
        survey.setBlockAbundanceSimulated(surveyDto.getBlockAbundanceSimulated());
        survey.setSurveyDate(parseDate(surveyDto.getSurveyDate()));
        survey.setSurveyTime(Time.valueOf(parseTime(surveyDto.getSurveyTime()).get()));
        survey.setDepth(Integer.valueOf(surveyDto.getDepth()));
        survey.setSurveyNum(surveyDto.getSurveyNum());

      if(StringUtils.isEmpty(surveyDto.getPqDiverInitials())) {
          survey.setPqDiverId(null);
        } else {
          List<Diver> diver = diverRepository.findByCriteria(surveyDto.getPqDiverInitials());
          if (diver.size() > 0) {
            Diver pqDiver = diver.get(0);
            survey.setPqDiverId(pqDiver.getDiverId());
          }
        }

        survey.setProjectTitle(surveyDto.getProjectTitle());
        survey.setProtectionStatus(surveyDto.getProtectionStatus());
        survey.setInsideMarinePark(surveyDto.getInsideMarinePark());
        survey.setNotes(surveyDto.getNotes());

        return survey;
    }

    public ValidationErrors validateSurvey(SurveyDto surveyDto) {
        List<ValidationError> errors = new ArrayList<>();
        Site surveyDtoSite = siteRepository.findBySiteCode(surveyDto.getSiteCode());
        Date surveyDate = null;

        // Date validations
        try {
            surveyDate = parseDate(surveyDto.getSurveyDate());
        } catch (DateTimeException e) {
            errors.add(new ValidationError("Survey", "surveyDate", surveyDto.getSurveyDate(),
                    e.getMessage()));
        }

        if(surveyDate != null) {
            if (surveyDate.after(new Date())) {
                errors.add(new ValidationError("Survey", "surveyDate", surveyDto.getSurveyDate(),
                        "A survey date cannot be in the future."));
            }

            if(surveyDto.getProgramId() != null && surveyDto.getProgramId() == RLS_PROGRAM_ID && 
                surveyDate.before(Date.from(LocalDate.of(2006, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                    errors.add(new ValidationError("Survey", "surveyDate", surveyDto.getSurveyDate(),
                            "A survey date cannot be before January 1st, 2006."));
            }
            
            if(surveyDto.getProgramId() != null && surveyDto.getProgramId() == ATRC_PROGRAM_ID && 
                surveyDate.before(Date.from(LocalDate.of(1991, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                    errors.add(new ValidationError("Survey", "surveyDate", surveyDto.getSurveyDate(),
                            "A survey date cannot be before January 1st, 1991."));
            }

        }

        // Time validations
        try {
          if (StringUtils.isEmpty(surveyDto.getSurveyTime()))
            surveyDto.getSurveyTime();
        } catch (DateTimeException e) {
            errors.add(new ValidationError("Survey", "surveyTime", surveyDto.getSurveyTime(),
                    "The survey time must be in the format hh:mm[:ss]"));
        }

        // Visibility validation
        if (!StringUtils.isBlank(surveyDto.getVisibility())) {
            Double vis = NumberUtils.toDouble(surveyDto.getVisibility(), Double.NEGATIVE_INFINITY);
            if (vis < 0) {
                errors.add(new ValidationError("Survey", "visibility", surveyDto.getVisibility(), (vis == Double.NEGATIVE_INFINITY) ? "Vis is not a decimal" : "Vis is not positive"));
            } else {
                if(vis.toString().split("\\.")[1].length() > 1)
                    errors.add(new ValidationError("Survey", "visibility", surveyDto.getVisibility(), "Vis is more than one decimal place"));
            }
        }

        // Lat and Lon match site
        Double lat = null;
        Double lon = null;
        try {
            lat = StringUtils.isEmpty(surveyDto.getLatitude()) ? null : Double.parseDouble(surveyDto.getLatitude());
        } catch (NumberFormatException e) {
            errors.add(new ValidationError("Survey", "latitude", surveyDto.getLatitude(),
                    "Latitude must contain a valid number"));
        }

        try {
            lon = StringUtils.isEmpty(surveyDto.getLongitude()) ? null : Double.parseDouble(surveyDto.getLongitude());
        } catch (NumberFormatException e) {
            errors.add(new ValidationError("Survey", "longitude", surveyDto.getLongitude(),
                    "Longitude must contain a valid number"));
        }

        boolean hasValidCoords = lat != null && lon != null && !lat.isNaN() && !lon.isNaN();
        double distMeters = hasValidCoords ? getDistanceLatLongMeters(lat, lon, surveyDtoSite.getLatitude(), surveyDtoSite.getLongitude()) : 0;
        if(distMeters > 200){
            errors.add(new ValidationError("Survey", "latitude", surveyDto.getLatitude(),
                    String.format("Coordinates are further than 200m from the Site (%.2fm)", distMeters)));
            errors.add(new ValidationError("Survey", "longitude", surveyDto.getLongitude(),
                    String.format("Coordinates are further than 200m from the Site (%.2fm)", distMeters)));
        }

        if(lat != null && !lat.isNaN() && (lat < -90 || lat > 90) ) {
            errors.add(new ValidationError("Survey", "latitude", surveyDto.getLatitude(),
                    "The latitude must be a valid number between -90 and 90"));
        }

        if(lon != null && !lon.isNaN() && (lon < -180 || lon > 180) ) {
            errors.add(new ValidationError("Survey", "longitude", surveyDto.getLongitude(),
                    "The longitude must be a valid number between -180 and 180"));
        }

        // Direction Validation
        if (surveyDto.getDirection() !=null && surveyDto.getDirection().length() > 0 && !EnumUtils.isValidEnum(Directions.class, surveyDto.getDirection())) {
            errors.add(new ValidationError("Survey", "direction", surveyDto.getDirection(),
                    surveyDto.getDirection() + " is invalid, expected: N,NE,E,SE,S,SW,W,NW, O or blank"));
        }

        // Ensure site/date/depth.surveyNum is unique
        Integer depth = surveyDto.getDepth() != null && surveyDto.getDepth().length() > 0 ? Integer.valueOf(surveyDto.getDepth()) : null;
        List<Survey> duplicateSurveys = surveyRepository.findBySiteDepthSurveyNumDate(
                surveyDtoSite, depth, surveyDto.getSurveyNum(), surveyDate).stream()
                .filter(s -> !s.getSurveyId().equals(surveyDto.getSurveyId()))
                .collect(Collectors.toList());

        if (duplicateSurveys.size() > 0) {
            errors.add(new ValidationError("Survey", "surveyNum", surveyDto.getSurveyNum().toString(),
                    String.format("A survey with the site, date and depth of \"%s/%s/%s.%s\" already exists.",
                            surveyDto.getSiteCode(), surveyDto.getSurveyDate(), surveyDto.getDepth(), surveyDto.getSurveyNum())));
        }

        return new ValidationErrors(errors);
    }
}
