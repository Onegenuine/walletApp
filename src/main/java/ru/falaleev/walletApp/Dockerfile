FROM tomcat:9.0.64

ADD ./target/walletApp-0.0.1-SNAPSHOT.jar /usr/local/tomcat/webapps/walletApp.war

EXPOSE 8080

CMD ["catalina.sh", "run"]