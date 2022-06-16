package ru.netology.web;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.conditions.ExactText;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;


import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;

public class CardDeliveryTest {
    private final DataGenerator.UserInfo validUser = DataGenerator.Registration.generateUser("Ru");
    private final int daysToAddFOrFirstMeeting = 5;
    private final String firstMeetingDate = DataGenerator.generateDate((daysToAddFOrFirstMeeting));

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Успешное планирование встречи")
    void shouldRegisterByAccount() {
        Configuration.holdBrowserOpen = true;

        $("[placeholder=\"Город\"]").setValue(validUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[placeholder=\"Дата встречи\"]").setValue(firstMeetingDate);
        $("[data-test-id=\"name\"] input").setValue(validUser.getName());
        $("[data-test-id=\"phone\"] input").setValue(validUser.getPhone());
        $("[data-test-id=\"agreement\"]").click();
        $x("//*[text()=\"Запланировать\"]").click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate));

        //Меняем дату
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String newDate = DataGenerator.generateDate(10);
        $("[placeholder=\"Дата встречи\"]").setValue(newDate);
        $x("//*[text()=\"Запланировать\"]").click();
        $("[data-test-id=replan-notification]")
                .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $x("//*[text()=\"Перепланировать\"]").click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + newDate));

    }

    @Test
    @DisplayName("Поялвение ошибки при вводе неверного формата мобильного телефона")
    void shouldGetErrorIfWrongPhone() {

        Configuration.holdBrowserOpen = true;

        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='phone'] input").setValue(DataGenerator.generatePhone("en"));
        $("[data-test-id='agreement']").click();
        $(byText("Запланировать")).click();
        $("[data-test-id='phone'] input_sub")
                .shouldHave(new ExactText("Неверный формат номера мобильного телефона"));
    }

}
