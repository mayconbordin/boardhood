__all__ = ['FIND_BY_KEY']

FIND_BY_KEY = """
        SELECT id, name, key, level
        FROM applications
        WHERE key = %s
"""
