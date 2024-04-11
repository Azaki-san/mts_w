package com.steam.mts_widget.services

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.stereotype.Service

@Service
class SBPService {
    fun getBillLink(price: Int, username: String): String? {
        val chromeOptions = ChromeOptions()
        chromeOptions.addArguments("--headless")
        chromeOptions.setExperimentalOption("excludeSwitches", listOf("enable-automation"))
        chromeOptions.setExperimentalOption("prefs", mapOf(
            "profile.managed_default_content_settings.images" to 2,
            "profile.managed_default_content_settings.javascript" to 2,
            "profile.managed_default_content_settings.css" to 2
        ))
        chromeOptions.addArguments("--disable-extensions")
        chromeOptions.addArguments("--disable-gpu")
        chromeOptions.addArguments("--no-sandbox")
        val driver: WebDriver = ChromeDriver(chromeOptions)

        driver.get("http://payment.mts.ru/cyber/steam/")
        Thread.sleep(1000)

        val inputFieldPrice = driver.findElement(By.name("price"))
        inputFieldPrice.sendKeys(Keys.BACK_SPACE.toString().repeat(4))
        inputFieldPrice.sendKeys(price.toString())

        val inputFieldLogin = driver.findElement(By.name("login"))
        inputFieldLogin.sendKeys(username)

        val payButton = driver.findElement(By.id("pay"))
        payButton.click()

        Thread.sleep(2500)

        val currentUrl = driver.currentUrl
        if (currentUrl.contains("qr.nspk.ru")) {
            return currentUrl
        }
        return null
    }
}