A little network analysis example. The data come from an appendix to David Hackett Fischer's *Paul Revere's Ride* (Oxford University Press, 1995). Put up to accompany this blog post: http://kieranhealy.org/blog/archives/2013/06/09/using-metadata-to-find-paul-revere/

## Prerequisites
* [Bundler](http://bundler.io/)

    ````    
    gem install bundler
    ````

## Getting started

* Download and install neo4j

    ````    
    ./scripts/installneo4.sh
    ````

* Start neo4j server
    
    ````
    ./neo4j-community-2.0.0-M04/bin/neo4j start
    ````

* Import Paul Revere data set
    
    ````
    bundle exec ruby scripts/import.rb
    ````

* Run JBLAS eigenvector centrality measure over the data set

    ````
    mvn clean compile assembly:single
    java -cp target/jblas-spike-1.0-SNAPSHOT-jar-with-dependencies.jar Neo4jAdjacencyMatrixSpike
    ````    