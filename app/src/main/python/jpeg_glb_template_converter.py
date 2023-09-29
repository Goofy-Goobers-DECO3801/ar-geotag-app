import pygltflib
import base64
from os.path import dirname, join


TEMPLATE_DISPLAY_IMAGE_INDEX = 2
TEMPLATE_FILE = "template.gltf"


def convert(input_file):
    template_file_name = join(dirname(__file__), TEMPLATE_FILE)
    model = pygltflib.GLTF2().load(template_file_name)
    display_img = model.images[TEMPLATE_DISPLAY_IMAGE_INDEX]

    with open(input_file, "rb") as file:
        replace_img_binary = file.read()

    replace_img_b64 = base64.b64encode(replace_img_binary).decode('utf-8')
    display_img.uri = f"data:image/jpeg;base64,{replace_img_b64}"

    glb = b"".join(model.save_to_bytes())

    return glb