# Flip URL Shortener Service

A simple URL shortener service built using Kotlin, Spring Boot, and PostgreSQL. This service takes a long URL and generates a shorter, more manageable link that redirects users to the original URL.

## Features

- Shortens a given long URL.
- Redirects from the shortened URL to the original URL.
- Uses PostgreSQL for data storage.
- Provides an API to create, retrieve, and delete shortened URLs.
- Dockerized for easy setup and deployment.

## Prerequisites

- Docker: Ensure Docker is installed and running on your system.
- Java 17: The project uses Java 17, so make sure it is installed.

## Getting Started

1. Clone repository
```
git clone https://github.com/yourusername/url-shortener.git
cd url-shortener
```

2. Docker setup

The project uses Docker to set up the application and the PostgreSQL database.
To start the services, run:
```
docker-compose up --build
```

This will start the following services:

- app: The Spring Boot application.
- db: A PostgreSQL database for storing URLs.

You can access the application at http://localhost:8080

3. Running tests

```
./gradlew test
```

4. To stop the services, run:

```
docker-compose down
```

## API Endpoints

The URL shortener exposes the following API endpoints:

- POST /api/urls: Shortens a given long URL.

Example using:
```
curl -X POST "http://localhost:8080/api/v1/shortener/shorten?longUrl=https://github.com"
```

- GET /{shortUrlId}: Redirects to the original long URL.

```
curl -X GET "http://localhost:8080/api/v1/shortener/3097fc"  
```

## Architecture and Design

The solution is designed with a RESTful API to handle URL shortening and redirection. The architecture follows a typical client-server model, where the server (Spring Boot) handles client requests for URL shortening and redirection.

### Components

- Spring Boot: Manages the web layer, service logic, and API routes for interacting with the application.
- PostgreSQL: Stores long URLs and their corresponding short URL identifiers.
- Docker: Containerizes the application and the PostgreSQL database, simplifying deployment and testing.
- H2 In-Memory Database: Used for running tests in an isolated environment.
- Gradle: Manages the project dependencies, builds, and testing.


### URL Shortening Strategy

This is the crucial point of this challenge. For my solution I went for simplicity over complexity due to the short nature
and timeline of this challenge. A hash algorithm using SHA-256 is used which is then truncated to smaller length (defaulted to 6).
Once generated, the short URL is stored in the PostgreSQL database along with the original long URL.
You can see the function below:

```kotlin
fun String.hash(truncate: Int = 6): String {
    // Hash the string using SHA-256
    val hashBytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray(Charsets.UTF_8))
    val hashString = StringBuilder().apply {
        hashBytes.forEach { byte -> append(String.format("%02x", byte)) }
    }.toString()
    return hashString.take(truncate)
}
```

### Collisions

One of the most important concerns when designing a URL shortener are collisions. In this context, a collision occurs 
when two different long URLs generate the same shortened URL. This can happen because hash functions, although deterministic, 
are not guaranteed to avoid collisions due to the finite number of possible outputs. Collisions are problematic because 
each shortened URL should uniquely represent one long URL, and if a collision occurs, the service may not know which long 
URL to redirect to. This challenges SHA-256 to hash the longUrl received. Furthermore, a constraint was added tho the short_url column
and check was added to the service to verify if the longUrl already existed in the database and simply returning it if so.


These choices were made specifically for simplicity and ease of implementation considering the time limitations of this 
challenge. While this solution mitigates this risk by truncating a SHA-256 hash and checking for existing URLs in the 
database, more robust approaches—such as appending random strings or user-specific identifiers—might be required in 
larger-scale systems to further reduce the chance of collisions.

### Performance considerations

As the dataset grows, the number of entries in the PostgreSQL database will increase, potentially affecting the 
performance of the application. Hashing the URL and checking for collisions introduces a performance overhead, especially 
if the number of stored URLs becomes large. To mitigate this, we could introduce database indexing on the short_url_id 
column, which would make lookups faster. Additionally, as part of future enhancements, we could explore techniques such 
as caching frequently used URLs or using NoSQL databases to optimize retrieval times.

## Future Enhancements

The current version of the URL shortener service fulfills basic requirements. However, given more time there are several 
features that I would implement for future versions:

- Expiration for URLs: Allow users to set expiration dates for their shortened URLs.
- Integration tests and end-to-end: To test the app more thoroughly 
- Authentication and User entity: This would allow multiple users to use the shortner which would also allow for longUrls to be repeated
- Improved hash algorithm and collision algorithm: With authentication and users the collision detection would need to be improved
to allow for longUrls to be equal. For this we could tweak the generateShortUrl method to continually check if the shortUrl
already exists on the database and to increment the truncate length if there shortUrl in question already exists. Example:
```kotlin
fun generateShortUrlId(longUrl: String): String {
    // Generate a short URL ID and check for collisions
    var shortUrlId: String
    var truncateLength = 6
    do {
        shortUrlId = longUrl.hash(truncateLength)
        truncateLength++
    } while (urlRepository.findByShortUrlId(shortUrlId).isPresent) // Keep checking for collisions
    return shortUrlId
}
```






