import numpy as np
from tree import *
from main import *

def find_features(node, id, opinions):
    if not node:
        return []
    if not (node.empty and node.left and node.right):
        return []

    rate = opinions[id][node.feature_index]
    if rate != rate:
        return [node.feature_index] + find_features(node.empty, id, opinions)
    elif rate < node.predicate:
        return [node.feature_index] + find_features(node.left, id, opinions)
    else:
        return [node.feature_index] + find_features(node.right, id, opinions)

def explain(user_tree, item_tree, user_opinion, item_opinion, user_id, item_id):
    user_features = find_features(user_tree.root, user_id, user_opinion)
    item_features = find_features(item_tree.root, item_id, item_opinion)

    shared_features = np.intersect1d(user_features, item_features)
    user_only = np.setdiff1d(user_features, shared_features)
    item_only = np.setdiff1d(item_features, shared_features)

    print 'We recommend item ', item_id, 'for user ', user_id, ' because:'
    for feature in shared_features:
        print 'user like the feature ', feature, ' in the item'
    for feature in user_only:
        print 'user like the feature ', feature
    for feature in item_only:
        print 'item has the feature ', feature

def recommend_for(pred_rating, user_tree, item_tree, user_opinion, item_opinion, user_id, k=5):
    result = np.argsort(-pred_rating[user_id])[0:k]
    print(user_id, result)
    for item_id in result:
        explain(user_tree, item_tree, user_opinion, item_opinion, user_id, item_id)

if __name__ == "__main__":

    # initialization
    parser = argparse.ArgumentParser()
    parser.add_argument("--path", help="result path", default="../results/")
    args = parser.parse_args()
    PATH = args.path

    user_tree = np.load(PATH + 'user_tree.npy', allow_pickle=True)[0]
    item_tree = np.load(PATH + 'item_tree.npy', allow_pickle=True)[0]

    item_vector = np.load(PATH + 'item_vector.npy')
    # item_vector = np.loadtxt(PATH + 'item_vector.txt')
    user_vector = np.load(PATH + 'user_vector.npy')
    # new_rating = np.dot(user_vector, item_vector.T)
    pred_rating = np.load(PATH + 'pred_rating.npy')

    user_opinion = np.load(PATH + 'user_opinion.npy')

    item_opinion = np.load(PATH + 'item_opinion.npy')

    print('### user_tree:')
    user_tree.better_print_tree(user_tree.root)
    print('### item_tree:')
    item_tree.better_print_tree(item_tree.root)
    recommend_for(pred_rating, user_tree, item_tree, user_opinion, item_opinion, 5)
# explain(0, 0)