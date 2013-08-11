A little network analysis example. The data come from an appendix to David Hackett Fischer's *Paul Revere's Ride* (Oxford University Press, 1995). Put up to accompany this blog post: http://kieranhealy.org/blog/archives/2013/06/09/using-metadata-to-find-paul-revere/

## Prerequisites
* [Bundler](http://bundler.io/)

## Getting started

* Download and install neo4j

    ./installneo4.sh

* Start neo4j server

    ./neo4j-community-2.0.0-M04/bin/neo4j start

* Import Paul Revere data set

    bundle exec ruby import.rb