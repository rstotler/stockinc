package com.jbs.StockGame.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.jbs.StockGame.entity.StockListing;

@Service
public class StockListingService {
    private List<StockListing> stockListings = new ArrayList<>();
    {
        stockListings.add(new StockListing("Cats", "CAT",
            Arrays.asList("Cat", "Cats", "Lion", "Lions", "Tiger", "Tigers")));
        stockListings.add(new StockListing("Dogs", "DOG",
            Arrays.asList("Dog", "Dogs", "Wolf", "Wolves")));
        stockListings.add(new StockListing("Cars", "AUTO",
            Arrays.asList("Car", "Cars", "Truck", "Trucks", "Lexus", "Lexus's", "Porsche", "Porsche's", "BMW", "BMW's", "Kia", "Kia's", "Tesla", "Tesla's", "Mercedes", "Audi", "Audi's", "Toyota", "Toyota's", "Subaru", "Subaru's", "Honda", "Honda's", "Mazda", "Mazda's", "Hyundai", "Hyundai's", "Nissan", "Nissan's", "Volkswagen", "Volkswagen's", "Volvo", "Volvo's", "Ford", "Ford's")));
        stockListings.add(new StockListing("Sony", "SONY",
            Arrays.asList("Sony", "Sony's", "Playstation", "Playstation's", "PS1", "PS1's", "PS2", "PS2's", "PS3", "PS3's", "PS4", "PS4's", "PS5", "PS5's", "Video Games")));
        stockListings.add(new StockListing("Microsoft", "MSFT",
            Arrays.asList("Microsoft", "Microsoft's", "Bill Gates", "XBOX", "XBOX's", "Video Games")));
        stockListings.add(new StockListing("Nintendo", "NTNDO",
            Arrays.asList("Nintendo", "Nintendo's", "NES", "NES's", "SNES", "SNES's", "Wii", "Wii's", "Video Games")));
        stockListings.add(new StockListing("Trump", "TRMP",
            Arrays.asList("Trump", "Trumps", "MAGA", "MAGAs")));
        stockListings.add(new StockListing("Biden", "BDN",
            Arrays.asList("Biden", "Bidens")));
        stockListings.add(new StockListing("America", "USA",
            Arrays.asList("America", "Americas", "American", "Americans", "United States", "USA")));
        stockListings.add(new StockListing("Russia", "RSSA",
            Arrays.asList("Russia", "Russia's", "Russian", "Russians")));
        stockListings.add(new StockListing("China", "CHNA",
            Arrays.asList("China", "China's", "Chinese")));
        stockListings.add(new StockListing("Space", "SPCE",
            Arrays.asList("Space", "Galaxy", "Galaxies", "Universe", "Universes", "Universe's", "Planet", "Planets", "Planet's", "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto", "Moon", "Moon's", "Black Hole", "Black Holes", "Supernova", "Meteor", "Meteors", "Meteorite", "Meteorites", "Asteroid", "Asteroids", "Quasar", "Quasars", "Sun", "Suns", "Sun's", "Star", "Stars", "Star's")));
        stockListings.add(new StockListing("Physics", "PHYS",
            Arrays.asList("Particle", "Particles", "Particle's", "Neutrino", "Neutrinos", "Molecule", "Molecules", "Atom", "Atoms", "Atomic", "Energy", "Mass", "Electron", "Electrons", "Neutron", "Neutrons", "Proton", "Protons", "Physics", "Einstein", "Metaphysics", "Quantum", "Entanglement")));
        stockListings.add(new StockListing("Elements", "ELMNT",
            Arrays.asList("Gold", "Gold", "Silver", "Silver", "Hydrogen", "Helium", "Carbon", "Aluminium", "Potassium", "Oxygen", "Nitrogen", "Sodium", "Calcium", "Iron", "Nickel", "Tin")));
        stockListings.add(new StockListing("Love", "LVE",
            Arrays.asList("Love", "Lust", "Emotion", "Emotions", "Passion", "Friendship", "Friend", "Romance", "Affection", "Sex", "Sexual", "Marriage", "Kiss", "Kisses", "Hug", "Hugs", "Heart", "Heart's", "Soul", "Souls", "Soulmate", "Soulmate's", "Dream", "Dreams", "God", "Heaven")));
        stockListings.add(new StockListing("Hate", "HTE",
            Arrays.asList("Hate", "Hates", "Hostile", "Hostility", "Dislike", "Fear", "Fears", "Death", "Deaths", "Afraid", "Scorn", "Scorns", "Despise", "Despises", "Bigotry", "Racism", "Racist", "Terror", "Cry", "Crying", "Disgust", "Crime", "Crimes", "Sadist", "Sadism", "Kill", "Killer")));
    }

    public List<StockListing> findAll() {
        return stockListings;
    }

    public StockListing findByName(String name) {
        return stockListings.stream().filter(stockListing -> stockListing.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    public StockListing findBySymbol(String symbol) {
        return stockListings.stream().filter(stockListing -> stockListing.getSymbol().equals(symbol))
            .findFirst()
            .orElse(null);
    }
}
