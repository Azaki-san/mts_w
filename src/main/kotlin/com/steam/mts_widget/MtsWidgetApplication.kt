package com.steam.mts_widget

import com.steam.mts_widget.services.StringToJsonService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.WebDriver

@SpringBootApplication
class MtsWidgetApplication {
	@Bean
	fun restTemplate(): RestTemplate {
		return RestTemplate()
	}

	@Bean
	fun stringToJson(): StringToJsonService {
		return StringToJsonService()
	}
}

fun main(args: Array<String>) {
	runApplication<MtsWidgetApplication>(*args)
}
