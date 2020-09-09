
alter table if exists nrmn.diver_ref_aud
    drop constraint if exists FK_DIVER_AUD_REV;

alter table if exists nrmn.location_ref_aud
    drop constraint if exists FK_LOCATION_AUD_REV;

alter table if exists nrmn.observable_item_ref_aud
    drop constraint if exists FK_OBS_ITEM_AUD_REV;

alter table if exists nrmn.observation_aud
    drop constraint if exists FK_OBS_AUD_REV;

alter table if exists nrmn.site_ref_aud
    drop constraint if exists FK_SITE_AUD_REV;

alter table if exists nrmn.survey_aud
    drop constraint if exists FK_SURVEY_AUD_REV;

alter table if exists nrmn.survey_method_aud
    drop constraint if exists FK_SURVEY_METHOD_AUD_REV;

drop table if exists nrmn.diver_ref_aud cascade;

drop table if exists nrmn.location_ref cascade;

drop table if exists nrmn.location_ref_aud cascade;


drop table if exists nrmn.observable_item_ref_aud cascade;

drop table if exists nrmn.observation_aud cascade;

drop table if exists nrmn.REVINFO cascade;


drop table if exists nrmn.site_ref_aud cascade;


drop table if exists nrmn.survey_aud cascade;

drop table if exists nrmn.survey_method_aud cascade;

drop table if exists nrmn.user_action_aud cascade;


create table nrmn.diver_ref_aud (
                                    diver_id int4 not null,
                                    REV int4 not null,
                                    REVTYPE int2,
                                    full_name varchar(255),
                                    fullName_MOD boolean,
                                    initials varchar(255),
                                    initials_MOD boolean,
                                    primary key (diver_id, REV)
);



create table nrmn.location_ref_aud (
                                       location_id int4 not null,
                                       REV int4 not null,
                                       REVTYPE int2,
                                       is_active boolean,
                                       isActive_MOD boolean,
                                       location_name varchar(255),
                                       locationName_MOD boolean,
                                       primary key (location_id, REV)
);


create table nrmn.observable_item_ref_aud (
                                              observable_item_id int4 not null,
                                              REV int4 not null,
                                              REVTYPE int2,
                                              obs_item_attribute uuid,
                                              obsItemAttribute_MOD boolean,
                                              observable_item_name varchar(255),
                                              observableItemName_MOD boolean,
                                              primary key (observable_item_id, REV)
);


create table nrmn.observation_aud (
                                      observation_id int4 not null,
                                      REV int4 not null,
                                      REVTYPE int2,
                                      measure_value int4,
                                      measureValue_MOD boolean,
                                      observation_attribute uuid,
                                      observationAttribute_MOD boolean,
                                      primary key (observation_id, REV)
);

create table nrmn.REVINFO (
                              REV int4 not null,
                              REVTSTMP int8,
                              primary key (REV)
);


create table nrmn.site_ref_aud (
                                   site_id int4 not null,
                                   REV int4 not null,
                                   REVTYPE int2,
                                   is_active boolean,
                                   isActive_MOD boolean,
                                   latitude float8,
                                   latitude_MOD boolean,
                                   longitude float8,
                                   longitude_MOD boolean,
                                   site_attribute jsonb,
                                   siteAttribute_MOD boolean,
                                   site_code varchar(255),
                                   siteCode_MOD boolean,
                                   site_name varchar(255),
                                   siteName_MOD boolean,
                                   primary key (site_id, REV)
);


create table nrmn.survey_aud (
                                 survey_id int4 not null,
                                 REV int4 not null,
                                 REVTYPE int2,
                                 depth int4,
                                 depth_MOD boolean,
                                 direction varchar(255),
                                 direction_MOD boolean,
                                 survey_attribute uuid,
                                 surveyAttribute_MOD boolean,
                                 survey_date date,
                                 surveyDate_MOD boolean,
                                 survey_num int4,
                                 surveyNum_MOD boolean,
                                 survey_time time,
                                 surveyTime_MOD boolean,
                                 visibility int4,
                                 visibility_MOD boolean,
                                 primary key (survey_id, REV)
);

create table nrmn.survey_method_aud (
                                        survey_method_id int4 not null,
                                        REV int4 not null,
                                        REVTYPE int2,
                                        block_num int4,
                                        blockNum_MOD boolean,
                                        survey_not_done boolean,
                                        surveyNotDone_MOD boolean,
                                        primary key (survey_method_id, REV)
);

create table nrmn.user_action_aud (
                                      id int8 not null,
                                      audit_time TIMESTAMP WITH TIME ZONE,
                                      details text,
                                      operation varchar(255),
                                      request_id varchar(255),
                                      username varchar(255),
                                      primary key (id)
);

alter table if exists nrmn.diver_ref_aud
    add constraint FK_DIVER_AUD_REV
        foreign key (REV)
            references nrmn.REVINFO;

alter table if exists nrmn.location_ref_aud
    add constraint FK_LOCATION_AUD_REV
        foreign key (REV)
            references nrmn.REVINFO;

alter table if exists nrmn.observable_item_ref_aud
    add constraint FK_OBS_ITEM_AUD_REV
        foreign key (REV)
            references nrmn.REVINFO;

alter table if exists nrmn.observation_aud
    add constraint FK_OBS_AUD_REV
        foreign key (REV)
            references nrmn.REVINFO;

alter table if exists nrmn.site_ref_aud
    add constraint FK_SITE_AUD_REV
        foreign key (REV)
            references nrmn.REVINFO;

alter table if exists nrmn.survey_aud
    add constraint FK_SURVEY_AUD_REV
        foreign key (REV)
            references nrmn.REVINFO;

alter table if exists nrmn.survey_method_aud
    add constraint FK_SURVEY_METHOD_AUD_REV
        foreign key (REV)
            references nrmn.REVINFO;