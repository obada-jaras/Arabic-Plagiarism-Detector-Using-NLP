# https://www.nltk.org/howto/stem.html
import sys
from nltk.stem.arlstem import ARLSTem


stemmer = ARLSTem()

sys.stdin.reconfigure(encoding='utf-8')
sys.stdout.reconfigure(encoding='utf-8')

sentence = sys.argv[1]

sentence = sentence.split(" ")
stemmedLine = [stemmer.stem(word) for word in sentence]
print(" ".join(stemmedLine))
