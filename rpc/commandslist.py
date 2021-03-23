# import requests to make our lives easier
import requests
# import socket to do IP address checks
import util
# import commands
import commands
import cmd_node
import cmd_quit
import cmd_close
import cmd_createwallet

# registers all commands to the command manager
def register_all(cmdManager):
    # this command sets the node ip and port
    cmdManager.register('setnode', cmd_node.parse)
    # this command exists the rpc client but does not affect the server
    cmdManager.register('quit', cmd_quit.parse)
    # this command exists the rpc client and sends a 'terminate' signal to the server
    cmdManager.register('close', cmd_close.parse)

    # wallet commands

    # this command generates a new wallet (name, (optional)encrypt)
    cmdManager.register('createwallet', cmd_createwallet.parse)
    # this command encrypts wallet (name)
    cmdManager.register('encryptwallet', cmd_createwallet.parse)