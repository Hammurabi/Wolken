# get the current working directory
import os
cwd = os.getcwd()
print(cwd)

# define helper functions
def findFile(dir, n):
    for entry in os.scandir(dir):
        if entry.is_dir() and n in entry.name:
            return os.path.join(dir, entry.name)
    return dir

# set the global variables
tools   = os.path.join(cwd, "tools")
if not os.path.exists(tools):
    os.makedirs(tools)

# download openjdk from link
import urllib.request
url     = ""
if 'win' in os.name:
    url = 'https://download.java.net/openjdk/jdk16/ri/openjdk-16+36_windows-x64_bin.zip'
elif 'mac' in os.name:
    print("could not download OpenJDK")
    print("no available options for Mac OS")
    print("please manually install a JDK")
    quit()
else:
    url = 'https://download.java.net/openjdk/jdk16/ri/openjdk-16+36_linux-x64_bin.tar.gz'

openjdk = os.path.join(cwd, "tools", "jdk.zip")
print("downloading openjdk from '" + url + "'")
urllib.request.urlretrieve(url, openjdk)
print("installing openjdk to '" + openjdk + "'")

# unzip openjdk to file 'openjdk'
import zipfile
print("unzipping package 'openjdk' to '" + os.path.join(tools, "openjdk") + "'")
with zipfile.ZipFile(openjdk, 'r') as zip:
    zip.extractall(tools)
file    = findFile(tools, 'jdk')
print("unzipped package 'openjdk' to '" + file + "'")
print("renaming file '" + file + "' to '" + os.path.join(tools, 'openjdk') + "'")
os.rename(file, os.path.join(tools, 'openjdk'))
print("renamed file '" + file + "' to '" + os.path.join(tools, 'openjdk') + "'")
print("deleting artifact '" + openjdk + "'")
os.remove(openjdk)
print("deleted artifact '" + openjdk + "'")