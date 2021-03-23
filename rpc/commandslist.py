# import requests to make our lives easier
import requests
# import socket to do IP address checks
import util
# import commands
import commands
import cmd_node

# registers all commands to the command manager
def register_all(cmdManager):
    register_node(cmdManager)
    register_getblock(cmdManager)
    register_gettx(cmdManager)