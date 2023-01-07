# https://github.com/linuxscout/qalsadi
import qalsadi.lemmatizer
import sys

sys.stdin.reconfigure(encoding='utf-8')
sys.stdout.reconfigure(encoding='utf-8')

sentence = sys.argv[1]

lemmer = qalsadi.lemmatizer.Lemmatizer()
lemmatizedLine = lemmer.lemmatize_text(sentence)
print(" ".join(lemmatizedLine))