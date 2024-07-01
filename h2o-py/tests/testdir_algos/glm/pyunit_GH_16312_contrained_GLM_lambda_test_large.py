import h2o
from h2o.estimators.glm import H2OGeneralizedLinearEstimator as glm
from tests import pyunit_utils
import numpy as np
import pandas as pd

# no need to check anything, this test just needs to run into completion without NPE error.
def data_prep(seed):
    np.random.seed(seed)
    x1 = np.random.normal(0, 10, 100000)
    x2 = np.random.normal(10, 100 , 100000)
    x3 = np.random.normal(20, 200, 100000)
    x4 = np.random.normal(30, 3000, 100000)
    x5 = np.random.normal(400, 4000, 100000)

    y_raw = np.sin(x1)*100 + np.sin(x2)*100 + x3/20 + x3/30 + x5/400
    y = np.random.normal(y_raw, 20)

    data = {
        'x1': x1,
        'x2': x2,
        'x3': x3,
        'x4': x4,
        'x5': x5,
        'y': y,
    }
    return h2o.H2OFrame(pd.DataFrame(data))

def test_bad_lambda_specification():
    train_data = data_prep(123)
    family = 'gaussian'
    link = 'identity'
    nfolds = 0
    lambda_ = 0.0
    seed = 1234
    calc_like = True
    compute_p_values = True
    solver = 'irlsm'
    predictors = ['x1', 'x2', 'x3', 'x4', 'x5']
    response = "y"

    linear_constraints2 = []
    
    name = "x2"
    values = 1
    types = "LessThanEqual"
    contraint_numbers = 0
    linear_constraints2.append([name, values, types, contraint_numbers])
    
    name = "x3"
    values = -1
    types = "LessThanEqual"
    contraint_numbers = 0
    linear_constraints2.append([name, values, types, contraint_numbers])
    
    name = "constant"
    values = 0
    types = "LessThanEqual"
    contraint_numbers = 0
    linear_constraints2.append([name, values, types, contraint_numbers])
    
    name = "x3"
    values = 1
    types = "LessThanEqual"
    contraint_numbers = 1
    linear_constraints2.append([name, values, types, contraint_numbers])
    
    name = "x4"
    values = -1
    types = "LessThanEqual"
    contraint_numbers = 1
    linear_constraints2.append([name, values, types, contraint_numbers])
    
    name = "constant"
    values = 0
    types = "LessThanEqual"
    contraint_numbers = 1
    linear_constraints2.append([name, values, types, contraint_numbers])
    
    name = "x2"
    values = 1
    types = "LessThanEqual"
    contraint_numbers = 2
    linear_constraints2.append([name, values, types, contraint_numbers])
    
    name = "x3"
    values = 1
    types = "LessThanEqual"
    contraint_numbers = 2
    linear_constraints2.append([name, values, types, contraint_numbers])
    
    name = "x4"
    values = 1
    types = "LessThanEqual"
    contraint_numbers = 2
    linear_constraints2.append([name, values, types, contraint_numbers])
    
    name = "constant"
    values = 0
    types = "LessThanEqual"
    contraint_numbers = 2
    linear_constraints2.append([name, values, types, contraint_numbers])
    
    
    linear_constraints = h2o.H2OFrame(linear_constraints2)
    linear_constraints.set_names(["names", "values", "types", "constraint_numbers"])

    linear_constraints = h2o.H2OFrame(linear_constraints2)
    linear_constraints.set_names(["names", "values", "types", "constraint_numbers"])
    # check lower bound of beta constraint will not generate error but lambda will.
    params = {
        "family" : family,
        "link": link,
        "lambda_" : lambda_,
        "seed" : seed,
        "nfolds" : nfolds,
        "compute_p_values" : compute_p_values,
        "calc_like" : calc_like,
        "solver" : solver,
        "linear_constraints": linear_constraints
    }

    model = glm(**params)
    model.train(x = predictors, y = response, training_frame = train_data)
    print(model.coef())
        

if __name__ == "__main__":
    pyunit_utils.standalone_test(test_bad_lambda_specification)
else:
    test_bad_lambda_specification()
