from itertools import chain

def merge_dict(x, y):
    return dict(chain(x.iteritems(), y.iteritems()))
