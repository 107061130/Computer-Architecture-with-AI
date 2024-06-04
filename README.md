# Computer Architecture with AI

## 學習高效能晶片設計實作的入門課程設計

  **High-Performance Computing (HPC)** 產業因為半導體產業的相關先進技術進步，所設計的晶片越來越複雜，對於一個想跨入這個產業的學習者，會面臨很多的困難，台灣由於半導體產業蓬勃發展，在晶片設計的後段設計生產製造方面，多年來培育了非常多不同領域的人才，但是在前段晶片設計與軟硬體系統整合發展上，一直比較受限，台灣產業的屬性與學校教育的環境在HPC這個領域並未開展開來，而HPC 晶片的開發因為複雜度相對於Edge Computing 晶片的複雜度高，需要很多新的設計概念與方法，以及具備更多垂直整合系統設計的要求，為了因應在人才培育上，HPC 的發展需要新的課程與方法，ACAL 準備了一系列的課程，希望能協助想進入HPC 前段設計的學習者，了解這個領域的基本技能需求，並建立一個共學的環境，讓對這個領域有熱情的同好，能有一個共同學習與做研究的平台。ACAL 的課程設計有幾個特色：

- **Hands-On Learning** - 作為其他理論課程的輔助，強調動手做學習，數位晶片設計的基礎包含邏輯設計、計算機組織與結構、C++/Python Programming，如果你沒有對應的相關基礎，學習這門課你會事倍功半，如果你已經有這些基礎，這門課的實作作業，對你來說會是很好把理論背景化為實踐能力的一個途徑。我們依然會複習很多基礎的邏輯設計、計算機組織與結構概念，但是輔以最新的硬體設計語言與設計方法，對你來說不會是一件很輕鬆的事情。但是如果你能順利完成並懂得所有Lab 作業的內容，你會對於之前學過的理論，會有全新的體會與認知。

- **Project-Based Learning** - ACAL curriculum 的所有Lab 是圍繞著**BlackBear** 這個研究與教學平台而設計的，**BlackBear** 是一個高效能人工智慧晶片計畫，與Meta 所發表的[MTIA v1: Meta’s first-generation AI inference accelerator](https://ai.meta.com/blog/meta-training-inference-accelerator-AI-MTIA/) 有著類似的系統架構，但是不同的細節設計，在開發這樣的晶片過程，有很多需要的技能，我們透過Lab 的設計，讓入門者能循序漸進習得相對所需的技能，目標希望這些Lab 能配育出參與這一類型HPC 晶片設計的前端人才。 

- **Learning How to Build Upon Open-Source Projects via TeamWork** - 開源晶片的研發最初始於加州大學柏克萊分校(University of California at Berkley)的一個學術專案，也是該機構，製造了第一個精簡指令集電腦(RISC) CPU：RISC-1。RISC-V最初旨在用作教學和研究工具。其指令集設計乾淨、簡單、先進且不存在IP的限制。它採用的開放方法允許研究人員建構晶片、擴展架構並探索新的指令，它甚至簡單到連正在學習的研究生也可以用它來設計。經過十幾年的發展，開源晶片已經深入影響業界產品的開發，隨著晶片的複雜度不斷提升，很多商業設計也必續站在巨人的肩膀上，利用開源軟體與硬體的基礎，來縮短設計週期與加速創新的腳步，也因此如何使用開源軟體與硬體，成為晶片設計人才技能樹的一個旁支，ACAL Curriculum 裡從使用開源軟體與硬體開始，進而希望學員有能力設計與貢獻開源軟體或硬體，學習如何在團隊共同開發大型專案，而不是一直只能關起門來做一人所及的小型專案，也是ACAL Curriculum 設計的宗旨之一。

- **Emphasizing Problem-Solving and Critical Thinking Skills** - 科技日新月異，晶片設計行業日新月異，不斷有新的工具軟體、設計方法出現，一個好的課程的設計最重要的是讓學員們透過課程的引導，最終學習到脫離課程後依然能有效的自學方法，雖然ACAL Curriculum 預定每兩年能針對產業狀況做一次更新，但是最好的方式，是學員經過Lab 的訓練能習得如何面對新學習議題時，能知道如何自學，我們期待ACAL curriculum 的學員們能自成一個學習的社群，在少數的Lab Assignment 裡， 我們會有團體作業，對於沒有標準答案的Assignment, 我們會有討論課來討論不同解題方式的思維與優劣，也因此不同學員來修課，往往所學程度可以有很大的差異，如果你有想新學的主題，我們也歡迎你來參與Lab 的設計，如果你想知道自己是否真的學會一個主題，嘗試著設計Lab 把別人教會，是一個絕佳驗證你自己學習成的機會。

- **Aligned with the Industrial Trends** - ACAL curriculum 每兩年會試圖更新一些新的設計工具與方法，盡量跟業界的標準產生鏈結，希望學員結業時，在投入業界工作時能有比較低的進入障礙，同時在比較深入的Lab 主題上，我們也會不斷更新引用的研究論文，提供給大家更多相關領域主題的研究參考資料。由於BlackBear 還是一個開發中的專案，在開發過程中，我們常常遇到社群裡的夥伴們有缺乏的背景知識，因而增加Lab 的內容來協助夥伴，ACAL Curriculum 從最早不到20 個Lab 開始，經過幾學期的進展，目前已經規劃成38個Lab，我們預期將來會有更多的更新與變動，也歡迎有興趣的學員，來跟我們共同開發ACAL Curriclum

## 開課目的

 基於上述的課程設計概念，我們開課的目的並非去取代現行既有的課程，而是希望協助學員在其他理論課程的基礎上，能把理論與實作做更好的鏈結與應用，這門課跟其他系統相關課程的不同著重之處如下:

- 著重於介紹人工智慧加速晶片設計的基本開發流程與實作
- 著重於軟硬體協同設計的概念
- 著重於理解通用型(general-purpose) 跟客製化(domain-specific) 設計上的差別
- 著重於介紹如何用軟體模擬硬體設計的方法

### AIAS 18週課程設計

人工智慧晶片設計橫跨多個領域的專業，在短短18週的課程中，很難一窺全貌，在這門課中，我們會以主題式學習的方式，來介紹一些基本的理論與實作基礎。我們的課程分為下面幾部分：

#### 基本理論教學
這門課每個階段的教學會有一個主題，可能是該主題的入門概論，可能是理論基礎課程，也可能是從實作面出發的主題討論課程，在期末專題提案之前，每一個探討的主題，都是為了協助同學了解人工智慧晶片設計的流程與基礎。

- **主題1. AI 模型與相關運算介紹** — 這個主題主要介紹Neural Netowrk 的模型運算都在做什麼？有什麼特性? AI 運算加速晶片主要要解決的問題是什麼

- **主題2. 數位設計基礎** — 為了設計基本的人工智慧加速功能，我們會介紹基礎的數位設計概念，包含Datapth 跟 Finite state machine Design，同時介紹一個硬體設計程式語言Chisel, 以及相關的實作。

- **主題3. 軟硬體協同設計基礎** — 人工智慧晶片的設計可以分為通用型與客製化的設計，也可能是混合型的異質性系統，無論是那一種設計方式，AI 的加速功能都是透過硬體與軟體的協作設計來達成的，在這個主題中，我們會透過介紹基本的RISC-V 微處理設計，來說明整個軟硬體協同設計的流程。

- **主題4. 人工智慧運算加速** — 最後這部分的主題我們用兩種不同的設計方式來介紹人工智慧運算晶片的兩種基本架構，先介紹如何在CPU內加入SIMD 指令利用Data parallelism 來加速，再介紹以Memory-Mapped I/O 的方式將一個Systolic-Array 型別的加速器與CPU 透過AXI-Lite Bus 做整合成一個小型的SOC來加速。

- **Programming Language Review** 

    在ACAL Curriculum 裡, 我們會用到多種程式語言，我們假設你對Python/C/C++/Verilog 有一定的基礎，能看懂簡單的程式並進行修改，Lab1-4 是針對一些基本的概念進行的測試，如果你做Lab1-4 覺得很困難，建議你先去補足一些背景知識再來修這門課
    - Lab 1-1 - Python Review 
    - Lab 1-2 - C/C++ Review
    - Lab 1-3 - Verilog Review
    - Lab 1-4 - ACAL Lab Readyness Accessment


- **Introduction to AI Models**

    - Lab 2 - Introduction to AI Models


- **Learning Chisel and Digital Design Review**

    - Lab 4 - Introduction to Chisel Programming - Datapath Design
    - Lab 5 - Introduction to Chisel Programming - FSM Design


- **Learning RISC-V and Computer Architecture Basics**

    - Lab 6 - RISC-V Instruction Set Architecture
    - Lab 7 - RISC-V Instruction Set Emulator
    - Lab 8 - Single-Cycle RISC-V CPU Design
    - Lab 9 - 5-Stage Pipelined RISC-V CPU Design

- **SOC Design**

    - Lab 11 - Bus Protocol and DMA Design


- **AI Computing Acceleration Basics**

    - Lab 13 - SIMD Instruction Design and Acceleration 
    - Lab 14 - Memory-Mapped Systolic Accelerator Design
