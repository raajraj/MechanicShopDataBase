#CS166 Project Phase 3

## MechanicShopDataBase
This PostgreSQL database It contains car, customer, and mechanic data that corresponds with service requests. It also contains functions that allow users to view and modify shop data. This includes inititating and closing service requests, and viewing specific customers.

### Starting your database..
Open the terminal to a PostgreSQL-enabled server. If you do not have PostgreSQL enabled, you can follow this download link (www.postgresql.org)  Use the following steps to clone the database's setup repository to your terminal:

```
$ git clone https://github.com/raajraj/MechanicShopDataBase.git
``` 
```
$ cd MechanicShopDataBase/cs-166_phase-3_code/code/postgresql/
```

Run the following commands to start your server:
```
$ source startPostgreSQL.sh
```
```
$ source createPostgreSQl.sh
```

This process should display all files that have been created in your databse.

### Running and Compiling
Use these commands to compile and run your database:

```
$ cd ..
```
```
$ cd java/
```
```
$ source compile.sh
```
```
$ source run.sh <db> <PORT> <user>
```
### Navigating

Running the database displays a menu with 10 options. The database is pre-loaded with sample data which can be modified using these functions. The menu has options to add to the databse, create service requests, close service requests, and list certain attributes. Select your choice in the menu by entering
the function number you want to access.

### Contributors

Raajitha Rajkumar - SID 862015848

Matthew Walsh - SID 862088280

*data and starter code provided by UC Riverside CS166*
