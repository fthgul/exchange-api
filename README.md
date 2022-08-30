
<br />
<p align="center">

<h3 align="center">Akinon Exchange Api Study Case </h3>

  <p align="center">
    Development a simple foreign exchange application which is one of the most
common services used in financial applications.
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contact">Contact</a></li>

  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

A simple foreign exchange application which is one of the most
common services used in financial applications. Requirements are as follows:

1. Exchange Rate API
   * []() input: source currency, target currency list
   * []() output: exchange rate list
2. Exchange API:
   * []() input: source amount, source currency, target currency list
   * []() output: amount list in target currencies and transaction id.
3. Exchange List API
   * []() input: transaction id or conversion date range (e.g. start date and end date)
   i. only one of the inputs shall be provided for each call
   * []() output: list of conversions filtered by the inputs


### Built With

* []()`java 17`
* []()`spring-boot 2.7.3 Version`
* []()`h2database (embedded)`

**Additional Information:**

* []() No Provides **swagger api** docs.
* []()Comprehensive unit tests for business requirements written using the **TDD** technique
* []()The **filter's** feature of the spring-boot was used to persist requests and responses.
* []()Added **Global Exception Handler** to be resilience




<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites
* java 17
* maven 3.3.6


### Installation
1.Execute the following docker command:
   ```sh
   mvn spring-boot:run
   ```



<!-- USAGE EXAMPLES -->
## Usage

After run the application, you can try to send a few http request the following:

###
---
GET http://localhost:8060/exchange-api/convert?sourceCurrency=USD&targetCurrencyList=TRY,EUR&amount=10
* []()`Content-Type: application/json`

###
---
GET http://localhost:8080/exchange-api/exchange-rates?sourceCurrency=USD&targetCurrencyList=TRY,EUR
* []()`Content-Type: application/json`


<!-- CONTACT -->
## Contact

Fatih Gül  `email:` fatihgul.elm@gmail.com
