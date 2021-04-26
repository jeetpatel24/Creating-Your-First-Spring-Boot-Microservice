package com.example.ec;

import com.example.ec.domain.Difficulty;
import com.example.ec.domain.Region;
import com.example.ec.service.TourPackageSevice;
import com.example.ec.service.TourService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class ExplorecaliApplication implements CommandLineRunner {

	//by implementing the CommandLineRunner we have to provide definition of run() method CommandLineRunner
	//In this definition of run() method we can load the Tour and TourPackage repositories like ew can load
	//the database at the appliaction startup

	@Value("ExploreCalifornia.json")
	private String importFile;

	@Autowired
	private TourService tourService;
	@Autowired
	private TourPackageSevice tourPackageService;

	public static void main(String[] args) { SpringApplication.run(ExplorecaliApplication.class, args); }

	@Override
	public void run(String... args) throws Exception {
		//since run method is at object scope we can access the injected services
		createTourPackages();
		long numberOfPackages = tourPackageService.total();
		createTours(importFile);
		long numberOfTours = tourService.total();
	}

	private void createTourPackages() {
		tourPackageService.createTourPackage("BC", "Backpack Cal");
		tourPackageService.createTourPackage("CC", "California Calm");
		tourPackageService.createTourPackage("CH", "California Hot springs");
		tourPackageService.createTourPackage("CY", "Cycle California");
		tourPackageService.createTourPackage("DS", "From Desert to Sea");
		tourPackageService.createTourPackage("KC", "Kids California");
		tourPackageService.createTourPackage("NW", "Nature Watch");
		tourPackageService.createTourPackage("SC", "Snowboard Cali");
		tourPackageService.createTourPackage("TC", "Taste of California");
	}

	private void createTours(String fileToFormat) throws IOException {
		TourFromFile.read(fileToFormat).forEach(importedTour ->
				tourService.createTour(importedTour.getTitle(),
						importedTour.getDescription(),
						importedTour.getBlurb(),
						importedTour.getPrice(),
						importedTour.getLength(),
						importedTour.getBullets(),
						importedTour.getKeywords(),
						importedTour.getPackageType(),
						importedTour.getDifficulty(),
						importedTour.getRegion()));
	}

	//helper class
	private static class TourFromFile {
		//fields
		private String packageType, title, description, blurb, price, length,
				bullets, keywords, difficulty, region;

		static List<TourFromFile> read(String fileToImport) throws IOException {
			return new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
					.readValue(new FileInputStream(fileToImport), new TypeReference<List<TourFromFile>>() {});
		}

		public String getPackageType() {
			return packageType;
		}

		public String getTitle() {
			return title;
		}

		public String getDescription() {
			return description;
		}

		public String getBlurb() {
			return blurb;
		}

		public Integer getPrice() {
			return Integer.parseInt(price);
		}

		public String getLength() {	return length; }

		public String getBullets() {
			return bullets;
		}

		public String getKeywords() {
			return keywords;
		}

		public Difficulty getDifficulty() {
			return Difficulty.valueOf(difficulty);
		}

		public Region getRegion() {
			return Region.findByLabel(region);
		}
	}
}
