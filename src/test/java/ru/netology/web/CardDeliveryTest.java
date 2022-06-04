package ru.netology.web;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;
import com.github.javafaker.Faker;

public class CardDeliveryTest {

    @Test
    void shouldRegisterByAccount() {
        String planningDate = DataGenerator.generateDate(5);
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");

        $("[placeholder=\"Город\"]").setValue(DataGenerator.generateCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[placeholder=\"Дата встречи\"]").setValue(planningDate);
        $("[data-test-id=\"name\"] input").setValue(DataGenerator.generateName("ru"));
        $("[data-test-id=\"phone\"] input").setValue(DataGenerator.generatePhone("ru"));
        $("[data-test-id=\"agreement\"]").click();
        $x("//*[text()=\"Запланировать\"]").click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + planningDate));

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

}
