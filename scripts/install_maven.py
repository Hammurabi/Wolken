# get the current working directory
import os
cwd = os.getcwd()
print(cwd)

# define helper functions
def findFile(dir, n):
    for entry in os.scandir(dir):
        if entry.is_dir() and n in entry.name:
            print(os.path.join(subdir, file))
            return os.path.join(subdir, file)
    return dir

# set the global variables
tools   = os.path.join(cwd, "tools")
if not os.path.exists(tools):
    os.makedirs(tools)

# download maven from link
import urllib.request
url     = 'https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip'
maven   = os.path.join(cwd, "tools", "maven.zip")
print("downloading maven from '" + url + "'")
urllib.request.urlretrieve(url, maven)
print("installing maven to '" + maven + "'")

# unzip maven to file 'maven'
import zipfile
print("unzipping package 'maven' to '" + os.path.join(tools, "maven") + "'")
with zipfile.ZipFile(maven, 'r') as zip:
    zip.extractall(os.path.join(tools, 'maven'))
print("unzipped package 'maven' to '" + tools + "'")
file    = findFile(tools, maven)
print("renaming file '" + file + "' to '" + os.path.join(tools, 'maven') + "'")