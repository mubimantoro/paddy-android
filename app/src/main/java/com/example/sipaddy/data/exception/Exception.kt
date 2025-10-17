package com.example.sipaddy.data.exception

class TokenExpiredException(message: String = "Token has expired") : Exception(message)
class UnauthorizedException(message: String = "Unauthorized") : Exception(message)