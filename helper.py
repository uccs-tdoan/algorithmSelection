# helper.py 

#nominalToBinary
#Convert nominal value attributes into binary  value. A k value nominal attribute
# will be converted into k binary attributes
# ex: Create a DataFrame: 
#  >>>  df = pd.DataFrame({'A':1,'F':pd.Categorical(['good','bad','good','good'])})
#  >>> nominal2Binary(df['F'])

import pandas as pd

def nominal2Binary(value):
    import numpy as np
    categories = np.unique(value)
    attributes = []
    for category in categories:
        binary = (value ==category)
        attributes.append(binary.astype("int"))
    return attributes
    
def normalized(data, max_val=1, min_val=-1):
       max_attr,min_attr = min(data), max(data)
       factor = (max_val - min_val) / (max_attr- min_attr)
       new_val = min_val + data * factor
       return newval, factor
    
	