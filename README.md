# StockInc
A web application created with Spring Boot, Thymeleaf, Java, Javascript, & Ajax

Gain prestige trading stocks, artificially influence market prices, gain market insight by leveraging tipsters, form companies with other players, and hack your competition and steal their resources!

Stocks are updated once per day. Stock prices rise and fall according to the number of times they are mentioned on https://brutalist.report, handled via web scraping.

## Login Screen
User data persisted via Spring Data JPA. Authentication is handled with Spring Security and hashed via BCrypt.

![](https://github.com/rstotler/gifs/blob/main/stockupdate2.png)

## Stock Listings
Custom user-data model including current account credit, current owned stocks, etc, are mapped via JPA/hibernate & JSON. View your account credit, total investment, and current gain/loss on the top bar. Stock listing prices determined by a 30-day rolling average updated every day via thread pool task scheduling via cronjobs & event publishing. Web elements such as ticker symbol & current price updated dynamically via Javascript.

![alt text](https://i.imgur.com/DvWhCjx.png)

## Player Companies
Band together with friends & other players by forming a company. Having more members increases your companies total value, resources, and hacking power. Modify existing company details & accept/decline new members as a company CEO via GET & POST requests. Consume resources (accept/decline/remove members) without refreshing the page via Ajax.

![](https://github.com/rstotler/gifs/blob/main/stockupdate1.png)

## Infrastructure Units
Using Javascript & timestamps, a dynamic countdown timer displays the remaining time in unit creation.

**Tipster** - Gain valuable market insight into future stock price movements.  
**Influencer** - Artificially influence the price of future stock movements.  
**Hacker** - Steal resources from competing companies to become the move valuable company.  
**Analyst** - Prevent hacks from occuring and gain valuable insight when they occur.  

![alt text](https://i.imgur.com/W3BSyDt.png)

## Hack Other Companies
Thread pool task scheduling & event publishing are also used when hacking competing groups. A javascript countdown timer is visible to dynamically display time remaining until the hack is over, at which time an event fires signalling the success or failure of the hack.

More hacker units increases chances of success. More analyst units increases defense against being hacked. Players can pool units together to use in increasingly larger company hacks. 

![alt text](https://i.imgur.com/B14kdX4.png)

## Message Inbox
Messages handled via event publishing and are tied to the player model data.

When market tipsters provide future stock movements via a published event, a message record is generated and stored using Spring Data JPA and SQL. These records are displayed as messages in the user's inbox. Successful and failed hacks detailing the amount of resources stolen are also displayed here.

![alt text](https://i.imgur.com/7k9y1W0.png)
