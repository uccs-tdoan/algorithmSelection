# plot for paper
library(ggplot2)
library(reshape2)
setwd("c:/algorithmSelection")
df <- read.csv("graph2.csv")
# Extract only two data type
realworld <- df[1:5,]
synthetic <- df[6:10,]


realworld <- melt(realworld, id='Dataset',variable_name='selection')
synthetic <- melt(synthetic, id='Dataset',variable_name='selection')
 
realworld$selection <- ifelse(realworld$value > 0.6,'YES','NO')
synthetic$selection <- ifelse(synthetic$value > 0.6,'YES','NO')

# Display value on top of bars
#p1 <- ggplot(realworld,aes(x=variable,y= value,fill=selection)) +geom_bar(stat='identity') +
   # ylab("Performance with SAR metric") + theme(axis.title.y=element_text(face='bold'))+
   # facet_grid(Dataset~.) + geom_text(aes(label=round(value,2)),size=5, vjust=-0.2) +
   # theme(axis.text.x=element_text(angle=90, size=10, vjust=0.5)) + scale_fill_grey(start = 0, end = .2)+
   # theme(plot.title = element_text(size=20, face="bold", vjust=2)) +
   # labs(x = "") + scale_y_continuous(breaks=c(0.25, 0.5, 0.75,1)) +
   # geom_hline(aes(yintercept=0.6), colour="#990000", linetype="dashed")

p1 <- ggplot(realworld,aes(x=variable,y= value,fill=selection)) +geom_bar(stat='identity') +
    ylab("Performance with SAR metric") + theme(axis.title.y=element_text(face='bold'))+
	ggtitle("Algorithms' performance on UCI datasets") +
	facet_grid(Dataset~.) + scale_fill_grey(start = 0, end = .6)+
	theme(axis.text.x=element_text(angle=90, size=12,face="bold", vjust=0.5)) + 
    theme(plot.title = element_text(size=20, face="bold", vjust=2)) +
    labs(x = "") + scale_y_continuous(breaks=c(0.25, 0.5, 0.75,1)) +
	geom_hline(aes(yintercept=0.6), colour="black", linetype="dashed")
  
p2 <- ggplot(synthetic,aes(x=variable,y= value,fill=selection)) +geom_bar(stat='identity') +
 ylab("Performance with SAR metric") + theme(axis.title.y=element_text(face='bold'))+
 ggtitle("Algorithms' performance on synthetic datasets") +  facet_grid(Dataset~.) + scale_fill_grey(start = 0, end = .6)+
 theme(axis.text.x=element_text(angle=90, size=12,face="bold", vjust=0.5)) +
 theme(plot.title = element_text(size=20, face="bold", vjust=2)) +
 labs(x = "") + scale_y_continuous(breaks=c(0.25, 0.5, 0.75,1)) +
 geom_hline(aes(yintercept=0.6), colour="black", linetype="dashed")

pdf("graphReal.pdf", width=6, height=4)
print(p1)
dev.off()

pdf("graphSynthetic.pdf", width=6, height=4)
print(p1)
dev.off()
 