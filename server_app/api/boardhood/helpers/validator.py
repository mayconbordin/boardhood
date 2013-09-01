def is_string(val):
    return isinstance(val, (basestring, str))

def is_integer(val):
    return isinstance(val, (int, long))

def is_numeric(val):
    try:
        float(val)
    except ValueError, TypeError:
        return False
    else:
        return True

def is_array(val):
    return isinstance(val, (list, tuple))
