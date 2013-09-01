import os, sys
from PIL import Image, ImageDraw
from flask import current_app as app

def get_extension(filename):
    if '.' in filename:
        return filename.rsplit('.', 1)[1].lower()
    else:
        return None

def resize(image, size=[128, 128]):
    try:
        src = app.config['RESOURCES_FOLDER'] + '/images/border.png'
        border = Image.open(src)
        source = border.convert('RGB')

        img = Image.open(image)
        img.thumbnail(size, Image.ANTIALIAS)
        img.paste(source, mask=border)
        return img
    except IOError, e:
        print e
        return False
