package au.org.aodn.nrmn.restapi.springdatarest;

import au.org.aodn.nrmn.restapi.model.db.LocationTestData;
import au.org.aodn.nrmn.restapi.model.db.SiteTestData;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.test.JwtToken;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import static au.org.aodn.nrmn.restapi.test.ApiUrl.entityRef;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresqlContainerExtension.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SiteApiIT {

    @LocalServerPort
    private int port;

    @Autowired
    private SiteTestData siteTestData;

    @Autowired
    private LocationTestData locationTestData;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private JwtToken jwtToken;

    private RequestSpecification spec;

    @BeforeEach
    public void setup() {
        spec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/sites")
                .setContentType("application/json")
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testPostSite() {
        val location = locationTestData.persistedLocation();

        Integer siteId = given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"TAS377\"," +
                        "\"siteName\": \"Low Islets\"," +
                        "\"longitude\": 147.7243," +
                        "\"latitude\": -40.13547," +
                        "\"location\": \"" + entityRef(port, "locations", location.getLocationId()) + "\"," +
                        "\"siteAttribute\": {" +
                        "    \"OldSiteCodes\": \"2102,7617\"," +
                        "    \"State\": \"Tasmania\"," +
                        "    \"Country\": \"Australia\"," +
                        "    \"ProtectionStatus\": \"Fishing\"," +
                        "    \"ProxCountry\": \"Australia\"" +
                        "}," +
                        "\"isActive\": true}")
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .path("siteId");

        val updatedSite = siteRepository.findById(siteId).get();

        assertThat(updatedSite.getSiteCode(), is(equalTo("TAS377")));
        assertThat(updatedSite.getSiteAttribute().get("OldSiteCodes"), is(equalTo("2102,7617")));
        assertThat(updatedSite.getLocation().getLocationId(), is(equalTo(location.getLocationId())));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testPutSite() {
        val site = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"TAS377\"," +
                        "\"siteName\": \"Low Islets\"," +
                        "\"longitude\": 147.7243," +
                        "\"latitude\": -40.13547," +
                        "\"siteAttribute\": {" +
                        "    \"OldSiteCodes\": \"2102,7617\"," +
                        "    \"State\": \"Tasmania\"," +
                        "    \"Country\": \"Australia\"," +
                        "    \"ProtectionStatus\": \"Fishing\"," +
                        "    \"ProxCountry\": \"Australia\"" +
                        "}," +
                        "\"isActive\": true}")
                .put(site.getSiteId().toString())
                .then()
                .assertThat()
                .statusCode(200);

        val updatedSite = siteRepository.findById(site.getSiteId()).get();

        assertThat(updatedSite.getSiteCode(), is(equalTo("TAS377")));
        assertThat(updatedSite.getSiteAttribute().get("OldSiteCodes"), is(equalTo("2102,7617")));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testPutSiteLocation() {
        val site = siteTestData.persistedSite();
        val newLocation = locationTestData.persistedLocation();

        given()
                .spec(spec)
                .contentType("text/uri-list")
                .auth()
                .oauth2(jwtToken.get())
                .body(entityRef(port, "locations", newLocation.getLocationId()))
                .put(site.getSiteId().toString() + "/location")
                .then()
                .assertThat()
                .statusCode(204);

        val updatedSite = siteRepository.findById(site.getSiteId()).get();

        assertThat(updatedSite.getLocation().getLocationId(), is(equalTo(newLocation.getLocationId())));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testCreateWithExistingSite() {
        val existingSite = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"" + existingSite.getSiteCode() + "\"," +
                        "\"siteName\": \"Low Islets\"," +
                        "\"longitude\": 147.7243," +
                        "\"latitude\": -40.13547," +
                        "\"location\": \"" + entityRef(port, "locations", existingSite.getLocation().getLocationId()) + "\"," +
                        "\"siteAttribute\": {" +
                        "    \"OldSiteCodes\": \"2102,7617\"," +
                        "    \"State\": \"Tasmania\"," +
                        "    \"Country\": \"Australia\"," +
                        "    \"ProtectionStatus\": \"Fishing\"," +
                        "    \"ProxCountry\": \"Australia\"" +
                        "}," +
                        "\"isActive\": true}")
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors[0].message", is(equalTo("a site with that code already exists")));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testUpdateWithExistingSite() {
        val site = siteTestData.persistedSite();
        val anotherSite = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"" + anotherSite.getSiteCode() + "\"," +
                        "\"siteName\": \"" + site.getSiteName() + "\"," +
                        "\"longitude\": " + site.getLongitude() + "," +
                        "\"latitude\": " + site.getLatitude() + "," +
                        "\"location\": \"" + entityRef(port, "locations", site.getLocation().getLocationId()) + "\"," +
                        "\"isActive\": " + site.getIsActive() + "}")
                .put(site.getSiteId().toString())
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors[0].message", is(equalTo("a site with that code already exists")));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testCreateWithMissingValues() {
        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{\"isActive\": true}")
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors.property", hasItems("siteCode", "siteName", "longitude", "latitude", "location"))
                .body("errors.message", contains("must not be null", "must not be null", "must not be null",
                        "must not be null", "must not be null"));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testDeleteSite() {
        val site = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .delete(site.getSiteId()
                            .toString())
                .then()
                .assertThat()
                .statusCode(204);

        val persistedSite = siteRepository.findById(site.getSiteId());

        assertFalse(persistedSite.isPresent());
    }
}