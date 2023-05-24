package nl.overheid.koop.plooi.dcn.integration.test.definitions;

import io.cucumber.java.en.And;
import nl.overheid.koop.plooi.dcn.integration.test.client.dcnadmin.DcnAdminClient;
import org.springframework.beans.factory.annotation.Autowired;

public class DcnAdminSteps {

    @Autowired
    private DcnAdminClient dcnAdminClient;

    @And("the user successfully logged in to DCN Admin Tools as {string} with password {string}")
    public void the_user_successfully_logged_in_to_dcn_admin_tools_as_with_password(String user, String password) {
        this.dcnAdminClient.login(user, password);
    }
}
