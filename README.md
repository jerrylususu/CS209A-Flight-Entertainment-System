# CS209A Project: Onboard Entertainment System

## Group Info
Group Number: 8
Group Members: Lu Zhirui, Zhou Ying, Li Zhao

## Acknowledgement
Prof. Stephane, TA Leo, other students, Youtube, CSDN and other web resources.
Special Thanks to Al-assad @ Github for his/her project "Simple-Media-Player". [Link to Github]("https://github.com/Al-assad/Simple-Media-Player") We used some code from this project and it helped a lot.

## Highlightes
1. Use properities file to make configuration process easier, no touch to source file required to add movie and change infomation.
2. Movie, Genre & Recommendation stored in .JSON files, thus making it simple to be read and modified.
3. Fully multi-language supporting with Resource Bundle with fallback.
4. Movie Info Scraper included, automatically scrape information from IMDB
5. Filter function implemented with Java 8's Lambda Expression and Predicate, allowing multi-condition filtering to find movie faster
6. Tried to use Material Design in GUI, using JFoenix [Link]("http://www.jfoenix.com/"), with CSS to add more artistic effect.

## Project Structure
`src`: Main Source Files
`View`: JavaFX Secne Description File (.FXML), Controllers
`Util`: Utilities & Entities
`Resources`: CSS, icon files

## How to config
1. Unzip the `CS209A Group8 jars.zip` to some directory.
2. Set the system environment variable: "moiveconf", with the value of the path to the external configuration file. (conf.properities)
3. Edit the config file, and make sure the airline logo, movie folder, genre file all exist on the directory described in the config file. (PS: use "/" as the path delimiter.)
4. Run the `System.jar` file (PS: make sure to use Java 8 runtime, or it won't start.)

## Group Work Distribution
All: UI Design, Control Flow Planning
Lu Zhirui: UI & LogicImplementation, Main Coder
Zhou Ying: Web Scraping, Data Structure Design
Li Zhao: UI Refining, QA
