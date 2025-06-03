package service;

import model.Location;
import persistence.LocationRepository;

import java.util.List;
import java.util.Optional;


public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService() {
        this.locationRepository = LocationRepository.getInstance();
    }

    public Location createLocation(String name, String type) {
        Location location = new Location(name, type);
        return locationRepository.save(location);
    }


    public Optional<Location> getLocationById(int locationId) {
        return locationRepository.findById(String.valueOf(locationId));
    }


    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }


    public List<Location> searchLocationsByName(String namePattern) {
        return locationRepository.findByName(namePattern);
    }


    public Location updateLocation(int locationId, String name, String type) {
        Optional<Location> locationOpt = locationRepository.findById(String.valueOf(locationId));
        if (locationOpt.isEmpty()) {
            throw new IllegalArgumentException("Location with ID " + locationId + " not found");
        }

        Location location = locationOpt.get();

        if (name != null && !name.isEmpty()) {
            location.setName(name);
        }

        if (type != null && !type.isEmpty()) {
            location.setType(type);
        }

        locationRepository.update(location);
        return location;
    }


    public void deleteLocation(int locationId) {
        Optional<Location> locationOpt = locationRepository.findById(String.valueOf(locationId));
        if (locationOpt.isEmpty()) {
            throw new IllegalArgumentException("Location with ID " + locationId + " not found");
        }

        locationRepository.delete(locationOpt.get());
    }
}
