from PIL import Image 
import pytesseract
filename = "C:/Users/김나연/Downloads/tesseract-test-image-3.jfif" 
image = Image.open(filename) 
text = pytesseract.image_to_string(image, lang= "kor") 
with open("sample.txt", "w") as f:
     f.write(text)
print(text)