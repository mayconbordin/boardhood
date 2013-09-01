#!/usr/bin/env python
"""
Amazon WS: Transfer files to an S3 bucket
Dependencies
    S3 http://aws.amazon.com/code/134
"""
import mimetypes
import os.path
import sys

sys.path.append('../..')
from boardhood.helpers import S3

AWS_ACCESS_KEY_ID = 'AKIAIHWEN3SLR5VBLN2Q'
AWS_SECRET_ACCESS_KEY = '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'
BUCKET_NAME = 'boardhood_profile_images'

def update_s3():
    conn = S3.AWSAuthConnection(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)

    filename = os.path.normpath('config.py')
    #if filename == '.' or not os.path.isfile(filename):
    #       continue
    print "Uploading %s" % filename
    filedata = open(filename, 'rb').read()
    content_type = mimetypes.guess_type(filename)[0]
    if not content_type:
        content_type = 'text/plain'
    conn.put(BUCKET_NAME, filename, S3.S3Object(filedata), {'x-amz-acl': 'public-read', 'Content-Type': content_type})

if __name__ == "__main__":
    update_s3()
