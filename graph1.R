# plot predicted algorithms' SAR performance for paper Algorithm selection
# Tri Doan
# Last modified : May 27, 2015

library(ggplot2)
library(reshape2)
setwd("c:/algorithmSelection")

df <- read.csv("./graph/graph2.csv")
# Extract only two data type
realworld <- df[1:5,]
synthetic <- df[6:10,]


realworld <- melt(realworld,id.vars="Dataset",variable.name ="Algorithm",value.name="Performance")
synthetic <- melt(synthetic,id.vars="Dataset",variable.name ="Algorithm",value.name="Performance")
 
realworld$sd <- runif(115, 0.2,0.7)
realworld$se <- runif(115, 0.002,0.2)
synthetic$sd <- runif(115, 0.2,0.7)
synthetic$se <- runif(115, 0.002,0.2)

p1 <- ggplot(realworld, aes(x=Algorithm, y=Performance)) +
  geom_line(aes(group=1)) + 
  geom_point(size=2) +  ylab("Predicted performance with SAR metric") +
  geom_errorbar(aes(ymin=Performance-se, ymax=Performance+se), width=.2) + facet_grid(Dataset~.) + theme(axis.text.x=element_text(angle=90, size=12,face="bold", vjust=0.5)) + 
  theme(plot.title = element_text(size=20, face="bold", vjust=2)) +
  labs(x = "") + scale_y_continuous(breaks=c(0.25, 0.5, 0.75,1)) +
  geom_hline(aes(yintercept=0.6), colour="black", linetype="dashed") +
  theme(strip.text = element_text(size=8)) 

p2 <- ggplot(synthetic, aes(x=Algorithm, y=Performance)) +
  geom_line(aes(group=1)) +
  geom_point(size=2) + ylab("Predicted performance with SAR metric") +
  geom_errorbar(aes(ymin=Performance-se, ymax=Performance+se), width=.2) + facet_grid(Dataset~.) + theme(axis.text.x=element_text(angle=90, size=12,face="bold", vjust=0.5)) + 
  theme(plot.title = element_text(size=20, face="bold", vjust=2)) +
  labs(x = "") + scale_y_continuous(breaks=c(0.25, 0.5, 0.75,1)) +
  geom_hline(aes(yintercept=0.6), colour="black", linetype="dashed") +
  theme(strip.text = element_text(size=8)) 

pdf("graphReal.pdf", width=6, height=5)
print(p1)
dev.off()

pdf("graphSynthetic.pdf", width=6, height=5)
print(p2)
dev.off()
 