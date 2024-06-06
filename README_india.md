[:us:](README.md) [:brazil:](README_ptbr.md) [:jp:](README_jp.md) [:cn:](README_cn.md)

# SnA*ke

SnA\*ke में आपका स्वागत है, यह A\* एल्गोरिदम का उपयोग करके क्लासिक सांप खेल का एक जावा स्विंग कार्यान्वयन है।

![Snake](https://i.imgur.com/TcbzVZL.png)

## विशेषताएं

- **स्मार्ट पाथफाइंडिंग**: सांप A* एल्गोरिदम का उपयोग करके खेल बोर्ड पर चालाकी से नेविगेट करता है, जाल और बाधाओं से बचते हुए सेब तक पहुंचने के लिए अपने पथ को अनुकूलित करता है।

- **जाल का पता लगाना**: एक अतिरिक्त हीयूरिस्टिक है जो संभावित फंसाने वाली स्थितियों से बचने का प्रयास करती है, जो ग्रिड के एक उपसेट की ओर ले जाने वाले चालों के लिए बहुत महंगा वज़न निर्धारित करती है जो वर्तमान सांप के आकार से छोटा है।

- **स्मार्ट लक्ष्य मूल्यांकन**: सांप सेब के चारों ओर उपलब्ध स्थानों का मूल्यांकन करता है, चाहे उसके चारों ओर संभावित भागने के लिए पर्याप्त स्थान हो या यदि इसके पास 3 या अधिक आसन्न वर्ग अवरुद्ध हों, यह भी इन संभावित चालों के लिए महंगे वज़न निर्धारित करके किया जाता है।

- **सत्र आंकड़े**: वर्तमान खेल की प्रगति और कई खेलों में औसत चाल और सांप के आकार को ट्रैक करें।


## स्थापना

स्थानीय रूप से SnA*ke चलाने के लिए, इन चरणों का पालन करें:

1. भंडार क्लोन करें:

   ```bash
   git clone https://github.com/TardivoJP/SnA-ke.git
   ```

2. परियोजना निर्देशिका पर जाएं:

   ```bash
   cd SnA-ke
   ```

3. खेल को संकलित और चलाएं:

   ```bash
   javac SnakeGUI.java
   java SnakeGUI
   ```


---

SnA*ke को देखने के लिए धन्यवाद! यदि आपके कोई प्रश्न या प्रतिक्रिया हैं, तो कृपया एक मुद्दा खोलें या संपर्क करें। खेल का आनंद लें! 🐍✨