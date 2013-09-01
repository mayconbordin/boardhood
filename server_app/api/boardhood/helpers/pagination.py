from math import ceil
from flask import request, url_for

from boardhood.helpers.date import now

class Pagination(object):
    def __init__(self, page, per_page, total_count):
        self.page = int(page)
        self.per_page = int(per_page)
        self.total_count = int(total_count)

    @property
    def pages(self):
        return int(ceil(self.total_count / float(self.per_page)))

    @property
    def has_prev(self):
        return self.page > 1

    @property
    def has_next(self):
        return self.page < self.pages

    @property
    def offset(self):
        return (self.page - 1) * self.per_page

    @property
    def page_valid(self):
        return self.page <= self.pages

    @property
    def info(self):
        return {
                'total': self.total_count,
                'page': self.page,
                'per_page': self.per_page,
                'url': request.url,
                'created_at': now()
        }

def paginate(args, total):
    pg = Pagination(args.page, args.per_page, total)
    return pg
