# import requests to make our lives easier
import requests
# import socket to do IP address checks
import util

class CommandsManager:
    def __init__(self):
        self.commands = []
    def register(self, command):
        self.commands.append(command)
    def parse(self, arguments):