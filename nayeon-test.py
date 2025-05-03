import pytesseract
import numpy as np
from PIL import Image

pytesseract.pytesseract.tesseract_cmd = 'C:/Program Files/Tesseract-OCR/tesseract.exe'

config=('-l kor --oem 3 --psm 6')


image_file = 'C:/Users/김나연/Downloads/tesseract-test-image.jpg'
image_path = 'C:/Users/김나연/Downloads/tesseract-test-image.jpg'
img = np.array(Image.open(image_path))

# OCR 실행 (문자열로 결과 반환)
raw_text = pytesseract.image_to_string(img, lang='kor')

# 공백 기준으로 배열 저장
text = raw_text.split()

# 텍스트 출력
print(text)