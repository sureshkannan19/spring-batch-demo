What is Spring Batch:- 
 Spring batch is used to process high volume of Data in Batches.
 * Job Flow
 * Transaction Management
 * Chunk Based Processing
 * Declarative I/O
 * Robust Error Handling
 * Scalability Options


**Spring Annotations and it's purpose:-**

## Important Annotations:-

* **@EnableBatchProcessing** :- adds many critical beans that support jobs, such as datasource, transaction management, etc.
* **@Configuration** :- To mark the class as a source of bean definitions.
* **@Autowired** :- Inject or wire the dependencies
* **@ComponentScan** :- Used to scan the component classes to load in **ApplicationContext**.
* **@EnableAutoConfiguration** :- This annotation tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
* **@SpringBootApplication** :- Combination of @Configuration, @EnableAutoConfiguration and @ComponentScan annotations.


* **@Bean** :- @Bean annotation works with @Configuration to create Spring beans.

* **@Qualifier** :- If multiple beans exists, Qualifier is used to filter which bean should be implemented.
* **@Value** :- Used to inject values from a property file. It supports both #{...} and ${...} placeholders.

## StereoType Annotations:-
* **@Component** :- Generic annotation. To create a instance of this class and inject the class using **@Autowired**
* **@Service, @Controller, @Repository:**- Specialized Stereotype(Standardized, Catagorized) Annotations.

For eg,  
* Repository should be used for Db Connections,
* Service should be used to write buisness logic.

**Refer Link:-** 
<a href="https://springframework.guru/spring-framework-annotations/">Here</a>
