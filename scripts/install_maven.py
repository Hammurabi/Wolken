# get the current working directory
import os
cwd = os.getcwd()
print(cwd)

# download maven from link
import urllib
url     = 'https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip'
maven   = os.path.join(cwd, "tools", "maven.zip")
print("downloading maven from '" + url + "'")
print("installing maven to '" + maven + "'")