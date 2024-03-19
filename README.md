# StockInc
A web application created with Spring Boot, Thymeleaf, Java, Javascript, & Ajax

Gain prestige trading stocks, artificially influence market prices, gain market insight buy leveraging tipsters, forming companies with other players, and hacking your competition and stealing their resources! Oh, and don't get caught, or your company could be disbanded or gain a negative repuation which negatively impacts your influence!

Stocks are updated once per day. Stock prices rise and fall according to the number of times they are mentioned in the news.

## Login Screen
User data persisted using Spring Data JPA. Authentication is handled using Spring Security and user details are hashed and protected.

![alt text](https://github.com/rstotler/gifs/blob/main/Login.png)

## Stock Listings
View your account credit, total investment, and gain/loss on the top bar. Stock listing prices determined by a 30-day rolling average updated every day. Buy/sell prices and other web elements are dynamically updated on mouse click using Javascript.

![alt text](https://github.com/rstotler/gifs/blob/main/Listings.png)

## Player Companies
Create player-made companies to band together with your friends and other players, increasing your company value and hacking power. Modify existing company details & accept/decline new members as a company CEO. Ajax is used to consume resources (accept/decline/remove members) without a page refresh.

![alt text](https://github.com/rstotler/gifs/blob/main/Companies.png)

## Infrastructure Units
Hire/create units to aid in your company endeavors. Javascript is used to create a dynamic countdown timer showing how much time remains in unit creation.

**Tipster** - Gain valuable market insight into future stock price movements.  
**Influencer** - Artificially influence the price of future stock movements.  
**Hacker** - Steal resources from competing companies to become the move valuable company.  
**Analyst** - Prevent hacks from occuring and gain valuable insight when they occur.  

![alt text](https://github.com/rstotler/gifs/blob/main/Infrastructure.png)

## Hack Other Companies
Hack competing groups using available hacker units and steal resources and stocks. More hackers increases chances of success. More analysts increases defense against being hacked. Countdown timer to hack completion is visible using Javascript to dynamically count down remaining time.

![alt text](https://github.com/rstotler/gifs/blob/main/Hacking.png)

## Message Inbox
Messages are received via leveraging market tipsters detailing future stock movements and upon successful and failed hacks, detailing the amount of resources stolen, and when being alerted to hacks by others.

![alt text](https://github.com/rstotler/gifs/blob/main/Messages.png)
