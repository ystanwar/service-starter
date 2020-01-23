# Best practices for service development
- Configuration
- Structured Logging
- Instrumentation
- Monitoring
- Health checks
- Resilience
- Code base structure
- CI 
- CD
- Security in CI/ CD
- Local env dev env setup support
- Service Registry
- Packaging 
- Packaging with containers

## Configuration
Configurations for applications and services help specificy initial settings and parameters for application/ service's behaviour at runtime. The basic idea to have configurations is to externalise these from code so that they can be changed without having to go thru a full build-test-deploy cycle for any changes in configuration.

Configurations, at the basic level are specified as named-parameter/ value pairs. There can be more refined configuration settings where the parameters are grouped together to sections and even further sub-sections in some cases.  

Tbd...
