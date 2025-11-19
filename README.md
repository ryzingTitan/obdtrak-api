# OBDTRAK API
___

This application returns data parsed from log files created by the [Torque](https://torque-bhp.com/) application
that has been stored in a PostgreSQL database.

#### Running locally

* Clone the repository: `git clone https://github.com/ryzingTitan/obdtrak-api.git`
* Navigate to the folder where the repository has been cloned: `cd obdtrak-api`
* Start a local PostgreSQL database in Docker `docker run --env=POSTGRES_PASSWORD=password -p 5432:5432  -d postgres:16.3-alpine`

### Acknowledgements
___
 
All the hard work done by the developers of the [Torque](https://torque-bhp.com/) application has enabled me to create this project.