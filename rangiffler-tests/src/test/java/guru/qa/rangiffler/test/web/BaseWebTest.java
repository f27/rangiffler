package guru.qa.rangiffler.test.web;

import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.page.LoginPage;
import guru.qa.rangiffler.page.MyTravelsPage;
import guru.qa.rangiffler.page.WelcomePage;

public abstract class BaseWebTest {

    protected static final Config CFG = Config.getInstance();

    protected final LoginPage loginPage = new LoginPage();
    protected final WelcomePage welcomePage = new WelcomePage();
    protected final MyTravelsPage myTravelsPage = new MyTravelsPage();

}
