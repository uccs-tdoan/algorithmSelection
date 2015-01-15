# plot combined Learner for paper
# Author: Tri doan
# Date: Jan 14, 2015

library(ggplot2)
library(reshape2)
setwd("c:/algorithmSelection")
#setwd("c:/algoSelecMeta")
df <- read.csv("graph3.csv")

df <- melt(df,id=c('Dataset','Type'))
# generate dot plot 

p <- ggplot(df, aes(x=variable, y=value)) + geom_point(aes(shape=Type),stat="identity") + 
  ylab("Performance with SAR metric") + theme(axis.title.y=element_text(face='bold'))+
  theme(plot.title = element_text(lineheight=.8, face="bold")) +
  theme(axis.text.x=element_text(angle=90, size=10, vjust=0.5)) +
  ggtitle("Combined Learners' performances on unseen dataset") +  facet_grid(. ~ Dataset)  + 
  labs(x = "") + geom_hline(aes(yintercept=0.6), colour="black", linetype="dashed") +
  theme(legend.position=c(0,1),legend.justification=c(0,1))
  
  
pdf("combinedLearner.pdf", width=6, height=4)
print(p)
dev.off()

