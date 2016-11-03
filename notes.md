# Before first demo/presentation

1. Ask the guys to timebox to 10 minutes their solution for the rules intersection problem. That way new, uninfluenced ideas will come up.

# TO DOs

- measure the performance of the app with junk data and context data? Use ec2. Is there a difference? Hypotheses: There will be difference because of difference in uniformity and stuff. So, in order to get correct performance benchmark fill the app with contextualize data (e.g. users with real names, real addresses etc).
- write README.md as if the library was finished, in order to see if it is clear how it's used
- Unit Tests 
- naming - Rule, exclusive rule; find better names, especially for 'exclusive rule'.

# Values

- easier to create examples with the running code.
  - better blog posts
  - better meetups/conference demos
- load testing
- integration testing

# Challenges

- hard to abstract - generics and reflection
- 
- tricky to unit test because of randomness (e.g. selecting random value from the allowed ranges)

# Advanced features

- corelation between feild rules: e.g. if user is older than 30, he must have defined highschoolName, otherwise highschoolNema is null;
- uniform and other distributions
- use instrumentation (byte budy) to create instance of the object instead of using reflection (for large loads, e.g. 1M of objects)
