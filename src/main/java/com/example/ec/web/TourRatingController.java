package com.example.ec.web;

import com.example.ec.domain.Tour;
import com.example.ec.domain.TourRating;
import com.example.ec.domain.TourRatingPk;
import com.example.ec.repo.TourRatingRepository;
import com.example.ec.repo.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("tours/{tourId}/ratings")
public class TourRatingController {
    private TourRatingRepository tourRatingRepository;
    private TourRepository tourRepository;

    @Autowired
    public TourRatingController(TourRatingRepository tourRatingRepository, TourRepository tourRepository) {
        this.tourRatingRepository = tourRatingRepository;
        this.tourRepository = tourRepository;
    }

    protected TourRatingController() {}

    @GetMapping
    public List<RatingDto> getAllRatingsForTour(@PathVariable int tourId) {
        verifyTour(tourId);
        return tourRatingRepository.findByPkTourId(tourId).stream()
                .map(RatingDto::new).collect(Collectors.toList());
    }

    @GetMapping("/average")
    public Map<String,Double> getAverage(@PathVariable int tourId) {
        verifyTour(tourId);
        return Map.of("Average", tourRatingRepository.findByPkTourId(tourId).stream().
                mapToInt(TourRating::getScore).average().
                orElseThrow(()->
                        new NoSuchElementException("Tour has no Ratings")));
    }

    @GetMapping("/page")
    public Page<RatingDto> getAllRatingsForTourByPage(@PathVariable("tourId") int tourId, Pageable pageable) {
        verifyTour(tourId);
        Page<TourRating> ratings = tourRatingRepository.findByPkTourId(tourId, pageable);
        return new PageImpl<>(
            ratings.get().map(RatingDto::new).collect(Collectors.toList()),pageable,ratings.getTotalElements()
            );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTourRating(@PathVariable("tourId") int tourId, @RequestBody @Validated RatingDto ratingDto) {
        Tour tour = verifyTour(tourId);
        tourRatingRepository.save(new TourRating(new TourRatingPk(tour, ratingDto.getCustomerId()),
                ratingDto.getScore(), ratingDto.getComment()));
    }

    @PutMapping
    public RatingDto updateWithPut(@PathVariable(value = "tourId") int tourId, @RequestBody @Validated RatingDto ratingDto) {
        TourRating rating = verifyTourRating(tourId, ratingDto.getCustomerId());
        rating.setScore(ratingDto.getScore());
        rating.setComment(ratingDto.getComment());
        return new RatingDto(tourRatingRepository.save(rating));
    }

    @PatchMapping
    public RatingDto updateWithPatch(@PathVariable(value = "tourId") int tourId, @RequestBody @Validated RatingDto ratingDto) {
        TourRating rating = verifyTourRating(tourId, ratingDto.getCustomerId());

        if(ratingDto.getScore() != null)
            rating.setScore(ratingDto.getScore());
        if(ratingDto.getComment() != null)
            rating.setComment(ratingDto.getComment());

        return new RatingDto(tourRatingRepository.save(rating));
    }

    @DeleteMapping("/{customerId}")
    public void delete(@PathVariable("tourId") int tourId, @PathVariable("customerId") int customerId) {
        TourRating rating = verifyTourRating(tourId,customerId);
        tourRatingRepository.delete(rating);
    }

    //helper method
    private Tour verifyTour(int tourId) {
        return tourRepository.findById(tourId).orElseThrow(()->
                new NoSuchElementException("Tour does not exist "+tourId));
    }
    //helper method
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String return400(NoSuchElementException ex) {
        return ex.getMessage();
    }
    //helper method
    private TourRating verifyTourRating(int tourId, int customerId) throws NoSuchElementException {
        return tourRatingRepository.findByPkTourIdAndPkCustomerId(tourId,customerId).
                orElseThrow(()-> new NoSuchElementException("Tour-Rating pair for request " + tourId + " for customer " + customerId));
    }
}
