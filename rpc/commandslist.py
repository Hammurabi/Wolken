# import requests to make our lives easier
import requests
# import socket to do IP address checks
import util
# import commands
import commands

# registers all commands to the command manager
def register_all(cmdManager):
    register_node(cmdManager)
    register_getblock(cmdManager)
    register_gettx(cmdManager)
def register_node(cmdManager):
    pass
def register_getblock(cmdManager):
    pass
def register_gettx(cmdManager):
    pass