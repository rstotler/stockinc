package com.jbs.StockGame.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.jbs.StockGame.entity.StockListing;
import com.jbs.StockGame.repository.StockListingRepository;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StockListingService {
    private StockListingRepository stockListingRepository;

    @PostConstruct
    public void postConstruct() {
        // stockListingRepository.save(new StockListing("Animals", "ANML",
        //     Arrays.asList("Dog", "Dogs", "Wolf", "Wolves", "Cat", "Cats", "Lion", "Lions", "Tiger", "Tigers", "Bear", "Bears")));
        // stockListingRepository.save(new StockListing("Cars", "AUTO",
        //     Arrays.asList("Car", "Cars", "Truck", "Trucks", "Lexus", "Lexus's", "Porsche", "Porsche's", "BMW", "BMW's", "Kia", "Kia's", "Tesla", "Tesla's", "Mercedes", "Audi", "Audi's", "Toyota", "Toyota's", "Subaru", "Subaru's", "Honda", "Honda's", "Mazda", "Mazda's", "Hyundai", "Hyundai's", "Nissan", "Nissan's", "Volkswagen", "Volkswagen's", "Volvo", "Volvo's", "Ford", "Ford's")));
        // stockListingRepository.save(new StockListing("Video Games", "GAME",
        //     Arrays.asList("Sony", "Sony's", "Playstation", "Playstation's", "PS1", "PS1's", "PS2", "PS2's", "PS3", "PS3's", "PS4", "PS4's", "PS5", "PS5's", "Microsoft", "Microsoft's", "Bill Gates", "XBOX", "XBOX's", "Nintendo", "Nintendo's", "NES", "NES's", "SNES", "SNES's", "Wii", "Wii's", "Video Games")));
        // stockListingRepository.save(new StockListing("Politics", "PLTC",
        //     Arrays.asList("Trump", "Trumps", "Trump's", "MAGA", "MAGAs", "Biden", "Bidens", "Biden's", "Election", "Elections")));
        // stockListingRepository.save(new StockListing("Space", "SPCE",
        //     Arrays.asList("Space", "Galaxy", "Galaxies", "Universe", "Universes", "Universe's", "Planet", "Planets", "Planet's", "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto", "Moon", "Moon's", "Black Hole", "Black Holes", "Supernova", "Meteor", "Meteors", "Meteorite", "Meteorites", "Asteroid", "Asteroids", "Quasar", "Quasars", "Sun", "Suns", "Sun's", "Star", "Stars", "Star's")));
        // stockListingRepository.save(new StockListing("Physics", "PHYS",
        //     Arrays.asList("Particle", "Particles", "Particle's", "Neutrino", "Neutrinos", "Molecule", "Molecules", "Atom", "Atoms", "Atomic", "Energy", "Mass", "Electron", "Electrons", "Neutron", "Neutrons", "Proton", "Protons", "Physics", "Einstein", "Metaphysics", "Quantum", "Entanglement")));
        // stockListingRepository.save(new StockListing("Elements", "ELMNT",
        //     Arrays.asList("Gold", "Gold", "Silver", "Silver", "Hydrogen", "Helium", "Carbon", "Aluminium", "Potassium", "Oxygen", "Nitrogen", "Sodium", "Calcium", "Iron", "Nickel", "Tin")));
        // stockListingRepository.save(new StockListing("Emotion", "EMO",
        //     Arrays.asList("Love", "Lust", "Emotion", "Emotions", "Passion", "Friendship", "Friend", "Romance", "Affection", "Sex", "Sexual", "Marriage", "Kiss", "Kisses", "Hug", "Hugs", "Heart", "Heart's", "Soul", "Souls", "Soulmate", "Soulmate's", "Dream", "Dreams", "God", "Heaven", "Hate", "Hates", "Hostile", "Hostility", "Dislike", "Fear", "Fears", "Death", "Deaths", "Afraid", "Scorn", "Scorns", "Despise", "Despises", "Bigotry", "Racism", "Racist", "Terror", "Cry", "Crying", "Disgust", "Crime", "Crimes", "Sadist", "Sadism", "Kill", "Killer")));
    }

    public List<StockListing> findAll() {
        return stockListingRepository.findAll();
    }

    public StockListing findByName(String name) {
        Optional<StockListing> stockListing = stockListingRepository.findByName(name);
        if(stockListing.isPresent()) {
            return stockListing.get();
        }

        return null;
    }

    public StockListing findBySymbol(String symbol) {
        Optional<StockListing> stockListing = stockListingRepository.findById(symbol);
        if(stockListing.isPresent()) {
            return stockListing.get();
        }

        return null;
    }

    public void addInfluencers(String username, String name, int count, String direction) {
        StockListing stock = findByName(name);
        Map<String, Integer> targetMap;
        if(direction.equals("Up")) {
            targetMap = stock.getInfluencerUpCount();
        } else {
            targetMap = stock.getInfluencerDownCount();
        }

        if(targetMap.containsKey(username)) {
            targetMap.put(username, targetMap.get(username) + count);
        } else {
            targetMap.put(username, count);
        }

        stockListingRepository.save(stock);
    }
}
