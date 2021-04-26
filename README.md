# ExploreCali

## This is the backend for California Tours where customer can get a list of tours from various Tour Packages.
## And also customer can rate it on the basis of his/her experienc on tours.



### Use an IDE (I used Intellij)
### `start the server`
### Now open postman


## 1 For Tours and Tour Packages

#### `GET`
##### Hit `http://localhost:8080/tourPackages` to get a list of Tour Package
##### Hit `http://localhost:8080/tours/search/findByTourPackageCode?code=BC` to get details of a particular Tour on the basis of tour code `BC`
##### Hit `http://localhost:8080/tours?size=1&page=1&sort=title,asc` to get a Page of Tours, Sorted by title
##### Hit `http://localhost:8080/tours/search/findByTourPackageCode?code=BC&size=3&sort=title,asc` to get a Page of Tours belonging to Tour Package code `BC`

#### `POST`
##### Hit `http://localhost:8080/tourPackages` with body (given below) to add a new tour Package
`{
    "code" : "ZC",
    "name" : "Zany Cali"
}`
#### `PUT`
##### Hit `http://localhost:8080/tourPackages` with body (given below) to update a Tour Package
`{
    code" : "ZC",
    "name" : "Zany California"
}`
#### DELETE
##### Hit `http://localhost:8080/tourPackages/ZC` to delete a Tour Package with code `ZC`


## 2 For Tour Ratings

#### `POST`
##### Hit `http://localhost:8080/tours/1/ratings` with body (given below) to Post a rating of a tour with tourId = 1
`{
    "score" : 5,
    "comment" : "It is the best tour",
    "customerId" : 123
}`



