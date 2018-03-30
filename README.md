# APIForRuleBasedSteemBots
This is repository for a voter bot on steem blockchain. 
His current functionalities are vote with choosed interval on people on the list. 
Interval can be set for each user separately.
Bot can be configured to vote on user every 24h.

When compiling you need org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file -Dfile=libs/steemj-core-0.5.0-SNAPSHOT.jar -DgroupId=eu.bittrade.libs -DartifactId=steemj-core -Dversion=0.5.0 -Dpackaging=jar -DlocalRepositoryPath=repo
to execute first.