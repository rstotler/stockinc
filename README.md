# StockInc
A web application created with Spring Boot, Thymeleaf, Java, Javascript, & Ajax

Gain prestige trading stocks, artificially influence market prices, gain market insight by leveraging tipsters, form companies with other players, and hack your competition and steal their resources!

Stocks are updated once per day. Stock prices rise and fall according to the number of times they are mentioned on https://brutalist.report, handled via web scraping.

## Login Screen
User data persisted using Spring Data JPA. Authentication is handled using Spring Security and hashed with BCrypt.

![](https://github.com/rstotler/gifs/blob/main/stockupdate2.png)

## Stock Listings
View your account credit, total investment, and current gain/loss on the top bar. Stock listing prices determined by a 30-day rolling average updated every day via thread pool task scheduling. Web elements such as ticker symbol & current price updated dynamically via Javascript.

![alt text](https://i.imgur.com/DvWhCjx.png)

## Player Companies
Band together with friends & other players by forming a company. Having more members increases your companies total value, resources, and hacking power. Modify existing company details & accept/decline new members as a company CEO via POST requests. Consume resources (accept/decline/remove members) without refreshing the page via Ajax.

![](https://github.com/rstotler/gifs/blob/main/stockupdate1.png)

## Infrastructure Units
Hire/create units to aid in your company endeavors. Javascript is used to create a dynamic countdown timer displaying the remaining time in unit creation.

**Tipster** - Gain valuable market insight into future stock price movements.  
**Influencer** - Artificially influence the price of future stock movements.  
**Hacker** - Steal resources from competing companies to become the move valuable company.  
**Analyst** - Prevent hacks from occuring and gain valuable insight when they occur.  

![alt text](https://i.imgur.com/W3BSyDt.png)

## Hack Other Companies
Hack competing groups using available hacker units and steal resources and stocks. More hackers increases chances of success. More analysts increases defense against being hacked. Countdown timer to hack completion is visible using Javascript to dynamically count down remaining time.

![alt text](https://i.imgur.com/B14kdX4.png)

## Message Inbox
Messages are received via leveraging market tipsters detailing future stock movements and upon successful and failed hacks, detailing the amount of resources stolen, and when being alerted to hacks by others.

![alt text](https://i.imgur.com/7k9y1W0.png)
