In this Spring-batch Demo project, i have implemented various features of SpringBatch, as follows:

* WriterBatchConfiguration - <a href="https://github.com/sureshbabk19698/spring-batch-demo/blob/main/src/main/java/com/subabk/writers/WriterBatchConfiguration.java">Implemented different types of Writers.</a>
* ReaderBatchConfiguration - <a href="https://github.com/sureshbabk19698/spring-batch-demo/blob/main/src/main/java/com/subabk/readers/ReaderBatchConfiguration.java">Implemented different types of Readers.</a>
* SkipBatchConfiguration -  <a href="https://github.com/sureshbabk19698/spring-batch-demo/blob/main/src/main/java/com/subabk/skip/SkipBatchConfiguration.java">Application executes without failing, based on defined Error and error limit.</a>
* JobDeciderBatchConfiguration - <a href="https://github.com/sureshbabk19698/spring-batch-demo/blob/main/src/main/java/com/subabk/jobdecider/JobDeciderBatchConfiguration.java">Application executes different steps based on condition.</a>
* PartitionBatchConfiguration - <a href="https://github.com/sureshbabk19698/spring-batch-demo/blob/main/src/main/java/com/subabk/partitioner/PartitionerBatchConfiguration.java">Implemented Multi-Threading</a>
* CacheBatchConfiguration - <a href="https://github.com/sureshbabk19698/spring-batch-demo/blob/main/src/main/java/com/subabk/cache/CacheBatchConfiguration.java">Implemented Caching.</a>
* StatefulBatchConfiguration - <a href="https://github.com/sureshbabk19698/spring-batch-demo/blob/main/src/main/java/com/subabk/restart/StatefulBatchConfiguration.java">Implemented Restartable approach - restarts the application from where it was failed at last run.</a>


Check here for Database Configuration: <a href="https://github.com/sureshbabk19698/spring-batch-demo/blob/main/src/main/java/com/subabk/config/DataSourceConfiguration.java">MySql</a> 

Table Definition :  <a href="https://github.com/sureshbabk19698/spring-batch-demo/blob/main/src/main/resources/schema.sql">Click Here</a> 

Models Used:- 
Citizen- <a href="https://github.com/sureshbabk19698/spring-batch-demo/blob/main/src/main/java/com/subabk/bo/Citizen.java">Click here</a>



