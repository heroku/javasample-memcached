#Sample usage of memcached on Heroku from java using spy memcached

    git clone
    heroku create -s cedar
    heroku addons:add memcache:5mb
    git push heroku master
    heroku run spy
